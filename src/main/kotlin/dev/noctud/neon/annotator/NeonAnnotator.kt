package dev.noctud.neon.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import dev.noctud.neon.ext.EnvFileParser
import dev.noctud.neon.ext.isPhpStan
import com.jetbrains.php.PhpIndex
import dev.noctud.neon.completion.ParameterCompletionProvider
import dev.noctud.neon.completion.PhpStanIdentifierCompletionProvider
import dev.noctud.neon.editor.NeonSyntaxHighlighter
import dev.noctud.neon.file.NeonFileType
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonArray
import dev.noctud.neon.psi.elements.NeonFile
import dev.noctud.neon.psi.elements.NeonKeyValPair

class NeonAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val type = element.node.elementType

        if (type === _NeonTypes.T_LITERAL || type === _NeonTypes.T_STRING) {
            highlightVariables(element, holder)
            if (type === _NeonTypes.T_LITERAL) {
                highlightNamedArgument(element, holder)
                highlightPhpStanIdentifier(element, holder)
                checkUnresolvedServiceRef(element, holder)
                checkUnresolvedClass(element, holder)
                highlightClassName(element, holder)
            }
        } else if (element is PsiErrorElement) {
            val prevSibling = element.prevSibling ?: return
            if (prevSibling.node.elementType === _NeonTypes.T_INDENT) {
                val indentText = prevSibling.text.replace("\n", "")
                if (indentText.isNotEmpty() && fileHasDifferentIndentBase(element, indentText[0])) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Invalid combination of tabs and spaces")
                        .range(element)
                        .create()
                }
            }
        } else if (element is NeonArray) {
            val arrayKeys = element.keys ?: return
            val keys: MutableSet<String?> = HashSet(arrayKeys.size)
            for (key in arrayKeys.filterNotNull()) {
                if (key.keyText == "-" || key.keyText?.startsWith("- ") == true) continue
                if (keys.contains(key.keyText)) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Duplicate key")
                        .range(key)
                        .create()
                } else {
                    keys.add(key.keyText)
                }
            }
        }
    }

    /**
     * Find %variable% patterns within a literal token, highlight them,
     * and warn if the variable is not defined.
     */
    private fun highlightVariables(element: PsiElement, holder: AnnotationHolder) {
        val text = element.text
        val baseOffset = element.textRange.startOffset
        var knownVars: Set<String>? = null
        var i = 0
        while (i < text.length) {
            // Skip escaped %% (literal percent in Neon)
            if (text[i] == '%' && i + 1 < text.length && text[i + 1] == '%') {
                i += 2
                continue
            }
            if (text[i] == '%' && i + 1 < text.length && isVarStartChar(text[i + 1])) {
                val end = text.indexOf('%', i + 1)
                if (end > i + 1 && !(end + 1 < text.length && text[end + 1] == '%')) {
                    val varName = text.substring(i + 1, end)
                    if (VARIABLE_NAME.matches(varName)) {
                        val range = TextRange(baseOffset + i, baseOffset + end + 1)

                        // Highlight the variable
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .range(range)
                            .textAttributes(NeonSyntaxHighlighter.VARIABLE)
                            .create()

                        // Check if variable is defined (lazy-load known vars)
                        if (knownVars == null) {
                            knownVars = collectKnownVariables(element)
                        }
                        if (!knownVars.contains(varName) && !varName.startsWith("env.")) {
                            holder.newAnnotation(HighlightSeverity.WARNING, "Parameter '%$varName%' might not be defined")
                                .range(range)
                                .create()
                        }

                        i = end + 1
                        continue
                    }
                }
            }
            i++
        }
    }

    private fun collectKnownVariables(element: PsiElement): Set<String> {
        val result = mutableSetOf<String>()
        val file = element.containingFile ?: return emptySet()
        if (file.isPhpStan()) {
            result.addAll(ParameterCompletionProvider.PHPSTAN_DEFAULTS)
            collectParametersFromFile(element.containingFile as? NeonFile, result)
        } else {
            result.addAll(ParameterCompletionProvider.NETTE_DEFAULTS)

            val project = element.project
            val scope = GlobalSearchScope.projectScope(project)
            val neonFiles = FileTypeIndex.getFiles(NeonFileType.INSTANCE, scope)
            for (vf in neonFiles) {
                if (vf.isPhpStan()) continue
                val psiFile = com.intellij.psi.PsiManager.getInstance(project).findFile(vf) as? NeonFile ?: continue
                collectParametersFromFile(psiFile, result)
            }

            // Add .env variables so they don't trigger false warnings
            result.addAll(EnvFileParser.collectEnvVariables(project))
        }

        return result
    }

    private fun collectParametersFromFile(file: NeonFile?, result: MutableSet<String>) {
        val rootValue = (file ?: return).value
        if (rootValue is NeonArray) {
            val map = rootValue.map ?: return
            val parametersValue = map["parameters"]
            if (parametersValue is NeonArray) {
                collectParameters(parametersValue, "", result)
            }
        }
    }

    private fun collectParameters(array: NeonArray, prefix: String, result: MutableSet<String>) {
        val keys = array.keys ?: return
        for (key in keys.filterNotNull()) {
            val keyText = key.keyText ?: continue
            val fullKey = if (prefix.isEmpty()) keyText else "$prefix.$keyText"
            result.add(fullKey)

            val parent = key.parent
            if (parent is NeonKeyValPair) {
                val value = parent.value
                if (value is NeonArray) {
                    collectParameters(value, fullKey, result)
                }
            }
        }
    }

    /**
     * Highlight PHPStan error identifier values (e.g., method.notFound, assign.propertyType)
     * at path: parameters → ignoreErrors → <bullet> → identifier:
     */
    private fun highlightPhpStanIdentifier(element: PsiElement, holder: AnnotationHolder) {
        val file = element.containingFile ?: return
        if (!file.isPhpStan()) return

        // Check if this literal is the VALUE of an "identifier" key
        // PSI path: T_LITERAL → SCALAR → VALUE → NAMED_KEY_VAL_PAIR
        // The VALUE node distinguishes it from the KEY position
        val scalar = element.parent ?: return
        if (scalar.node.elementType !== _NeonTypes.SCALAR) return
        val value = scalar.parent ?: return
        if (value.node.elementType !== _NeonTypes.VALUE) return
        val namedKvp = value.parent ?: return
        if (namedKvp !is NeonKeyValPair) return
        if (namedKvp.keyText != "identifier") return

        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element)
            .textAttributes(NeonSyntaxHighlighter.PHPSTAN_IDENTIFIER)
            .create()

        // Warn if the identifier doesn't match any known PHPStan identifier
        val text = element.text
        if (!PhpStanIdentifierCompletionProvider.KNOWN_IDENTIFIERS.contains(text)) {
            val prefix = text.substringBefore(".")
            if (!PhpStanIdentifierCompletionProvider.PHPSTAN_IDENTIFIER_PREFIXES.contains(prefix)) {
                // Completely unknown prefix — definitely wrong
                holder.newAnnotation(HighlightSeverity.WARNING, "Unknown PHPStan error identifier '$text'")
                    .range(element)
                    .create()
            } else {
                // Known prefix but unknown specific identifier — might be valid
                holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Unrecognized PHPStan error identifier '$text'")
                    .range(element)
                    .create()
            }
        }
    }

    /**
     * Highlight named arguments inside entity parameters: Foo(name: value)
     * The "name" key gets PHP-style named argument color.
     */
    private fun highlightNamedArgument(element: PsiElement, holder: AnnotationHolder) {
        // T_LITERAL → SCALAR → ARRAY_KEY → ARRAY_KEY_VALUE_PAIR → ARRAY_VALUE → ENTITY_PARAMETERS
        // or T_LITERAL → SCALAR → ARRAY_KEY → ARRAY_KEY_VALUE_PAIR → ARRAY_VALUE → INLINE_ARRAY
        // We need to check that this literal is a key inside () or [] or {}
        val scalar = element.parent ?: return
        if (scalar.node.elementType !== _NeonTypes.SCALAR) return
        val arrayKey = scalar.parent ?: return
        if (arrayKey.node.elementType !== _NeonTypes.ARRAY_KEY) return

        // Walk up to find if we're inside ENTITY_PARAMETERS or INLINE_ARRAY
        var parent = arrayKey.parent
        while (parent != null) {
            val parentType = parent.node.elementType
            if (parentType === _NeonTypes.ENTITY_PARAMETERS) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element)
                    .textAttributes(NeonSyntaxHighlighter.NAMED_ARGUMENT)
                    .create()
                return
            }
            if (parentType === _NeonTypes.ARRAY || parentType === _NeonTypes.INNER_ARRAY) {
                return // top-level key, not a named argument
            }
            parent = parent.parent
        }
    }

    /**
     * Check @serviceRef references — warn if the service is not defined
     * in any services: section and is not a valid PHP class.
     */
    private fun checkUnresolvedServiceRef(element: PsiElement, holder: AnnotationHolder) {
        val text = element.text
        if (!text.startsWith("@") || text.length < 2) return
        // @Class\With\Namespace is handled by checkUnresolvedClass — skip those here
        if (text.contains("\\")) return

        // Strip @ and ::method suffix
        val serviceName = text.substring(1).substringBefore("::")

        // Check if the service is defined in any neon file's services: section
        val knownServices = collectKnownServices(element)
        if (!knownServices.contains(serviceName)) {
            holder.newAnnotation(HighlightSeverity.WARNING, "Service '@$serviceName' not found")
                .range(element)
                .create()
        }
    }

    private fun collectKnownServices(element: PsiElement): Set<String> {
        val result = mutableSetOf<String>()
        val project = element.project
        val scope = GlobalSearchScope.projectScope(project)
        val neonFiles = FileTypeIndex.getFiles(NeonFileType.INSTANCE, scope)
        for (vf in neonFiles) {
            if (vf.isPhpStan()) continue
            val psiFile = com.intellij.psi.PsiManager.getInstance(project).findFile(vf) as? NeonFile ?: continue
            val rootValue = psiFile.value
            if (rootValue is NeonArray) {
                val map = rootValue.map ?: continue
                val servicesValue = map["services"]
                if (servicesValue is NeonArray) {
                    val keys = servicesValue.keys ?: continue
                    for (key in keys.filterNotNull()) {
                        val keyText = key.keyText ?: continue
                        if (keyText != "-") result.add(keyText)
                    }
                }
            }
        }
        return result
    }

    /**
     * Apply enforced class color to PHP class references:
     * - FQN classes with \ (e.g., App\Model\Foo)
     * - Simple PascalCase names that resolve in PhpIndex (e.g., Throwable, Exception)
     * Skips keys, service refs (@), paths, variables, wildcards.
     */
    private fun highlightClassName(element: PsiElement, holder: AnnotationHolder) {
        val text = element.text
        if (text.startsWith("@") || text.contains("/") || text.contains("%") || text.contains("*")) return

        // Skip if inside a KEY node
        val parent = element.parent
        if (parent != null && (parent.node.elementType === _NeonTypes.KEY || parent.node.elementType === _NeonTypes.ARRAY_KEY)) return
        val grandparent = parent?.parent
        if (grandparent != null && (grandparent.node.elementType === _NeonTypes.KEY || grandparent.node.elementType === _NeonTypes.ARRAY_KEY)) return

        val isClass = if (text.contains("\\")) {
            true // FQN — always color as class
        } else if (looksLikeSimpleClassName(text)) {
            // Simple name — check PhpIndex
            val phpIndex = PhpIndex.getInstance(element.project)
            phpIndex.getClassesByName(text).isNotEmpty()
                || phpIndex.getInterfacesByName(text).isNotEmpty()
                || phpIndex.getTraitsByName(text).isNotEmpty()
        } else {
            false
        }

        if (isClass) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(element)
                .enforcedTextAttributes(NeonSyntaxHighlighter.CLASSNAME.defaultAttributes)
                .create()
        }
    }

    /**
     * Check if a literal containing \ (PHP class FQN) resolves to an actual class.
     * Warns if the class cannot be found.
     */
    private fun checkUnresolvedClass(element: PsiElement, holder: AnnotationHolder) {
        val text = element.text
        if (!text.contains("\\")) return

        // Strip @ prefix for service references and ::method suffix
        var fqn = if (text.startsWith("@")) text.substring(1) else text
        fqn = fqn.substringBefore("::")

        // Skip if it looks like a path or contains %variables%
        if (fqn.contains("/") || fqn.contains("%")) return

        // Skip wildcards like *Data, *DTO
        if (fqn.contains("*")) return

        val phpIndex = PhpIndex.getInstance(element.project)
        var classes = phpIndex.getAnyByFQN(fqn)
        if (classes.isEmpty() && !fqn.startsWith("\\")) {
            classes = phpIndex.getAnyByFQN("\\$fqn")
        }

        if (classes.isEmpty()) {
            // Check if it's a namespace or a prefix of a namespace
            val fqnWithSlash = "\\" + fqn.trimStart('\\')
            val namespaces = phpIndex.getNamespacesByName(fqnWithSlash)
            if (namespaces.isEmpty()) {
                // Also check if any namespace starts with this prefix
                val hasChildNamespaces = phpIndex.getChildNamespacesByParentName(fqnWithSlash + "\\").isNotEmpty()
                if (!hasChildNamespaces) {
                    holder.newAnnotation(HighlightSeverity.WARNING, "Class or namespace '$fqn' not found")
                        .range(element)
                        .create()
                }
            }
        }
    }

    private fun isVarStartChar(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c == '_'
    }

    private fun fileHasDifferentIndentBase(element: PsiElement, indentChar: Char): Boolean {
        val fileText = element.containingFile?.text ?: return false
        var i = 0
        while (i < fileText.length) {
            if (fileText[i] == '\n' && i + 1 < fileText.length) {
                val nextChar = fileText[i + 1]
                if ((nextChar == '\t' || nextChar == ' ') && nextChar != indentChar) {
                    return true
                }
            }
            i++
        }
        return false
    }

    companion object {
        private val VARIABLE_NAME = Regex("[a-zA-Z_][a-zA-Z0-9._-]*")
        val SIMPLE_CLASS_NAME = Regex("[A-Z][a-zA-Z0-9_]+")

        /** Check if a literal looks like a simple PHP class name (PascalCase, no special chars) */
        fun looksLikeSimpleClassName(text: String): Boolean {
            if (text.contains("\\") || text.contains("@") || text.contains("%") ||
                text.contains("/") || text.contains(".") || text.contains("*") ||
                text.isEmpty() || !text[0].isUpperCase()) return false
            return SIMPLE_CLASS_NAME.matches(text)
        }
    }
}
