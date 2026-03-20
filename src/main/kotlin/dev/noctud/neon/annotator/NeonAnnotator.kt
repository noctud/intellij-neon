package dev.noctud.neon.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import dev.noctud.neon.completion.ParameterCompletionProvider
import dev.noctud.neon.editor.NeonSyntaxHighlighter
import dev.noctud.neon.file.NeonFileType
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonArray
import dev.noctud.neon.psi.elements.NeonFile
import dev.noctud.neon.psi.elements.NeonKeyValPair

class NeonAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val type = element.node.elementType

        if (type === _NeonTypes.T_LITERAL) {
            highlightVariables(element, holder)
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
            if (text[i] == '%' && i + 1 < text.length && isVarStartChar(text[i + 1])) {
                val end = text.indexOf('%', i + 1)
                if (end > i + 1) {
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
        val fileName = element.containingFile?.name ?: ""
        val isPhpStan = fileName.contains("phpstan", ignoreCase = true) && fileName.endsWith(".neon")

        if (isPhpStan) {
            result.addAll(ParameterCompletionProvider.PHPSTAN_DEFAULTS)
            collectParametersFromFile(element.containingFile as? NeonFile, result)
        } else {
            result.addAll(ParameterCompletionProvider.NETTE_DEFAULTS)

            val project = element.project
            val scope = GlobalSearchScope.projectScope(project)
            val neonFiles = FileTypeIndex.getFiles(NeonFileType.INSTANCE, scope)
            for (vf in neonFiles) {
                if (vf.name.contains("phpstan", ignoreCase = true)) continue
                val psiFile = com.intellij.psi.PsiManager.getInstance(project).findFile(vf) as? NeonFile ?: continue
                collectParametersFromFile(psiFile, result)
            }
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
    }
}
