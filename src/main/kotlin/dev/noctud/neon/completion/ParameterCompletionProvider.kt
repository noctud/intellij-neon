package dev.noctud.neon.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.ProcessingContext
import dev.noctud.neon.file.NeonFileType
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonArray
import dev.noctud.neon.psi.elements.NeonFile
import dev.noctud.neon.psi.elements.NeonKeyValPair

/**
 * Provides autocompletion for %variable% parameters in Neon files.
 *
 * For Nette config files: suggests Nette defaults + user-defined parameters from `parameters:` sections.
 * For PHPStan config files (*phpstan*.neon): suggests PHPStan-specific defaults + parameters from the file.
 */
class ParameterCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        results: CompletionResultSet
    ) {
        val element = params.position
        val elementType = element.node.elementType

        // Only complete inside literals that start with %
        if (elementType !== _NeonTypes.T_LITERAL) return
        val text = element.text
        if (!text.startsWith("%")) return

        val fileName = element.containingFile?.name ?: ""
        val isPhpStan = fileName.contains("phpstan", ignoreCase = true) && fileName.endsWith(".neon")

        val variables = mutableSetOf<String>()

        if (isPhpStan) {
            variables.addAll(PHPSTAN_DEFAULTS)
            // Also collect parameters defined in this PHPStan config
            collectParametersFromFile(element.containingFile as? NeonFile, variables)
        } else {
            variables.addAll(NETTE_DEFAULTS)
            // Collect user-defined parameters from all non-phpstan neon files in the project
            val project = element.project
            val scope = GlobalSearchScope.projectScope(project)
            val neonFiles = FileTypeIndex.getFiles(NeonFileType.INSTANCE, scope)

            for (vf in neonFiles) {
                if (vf.name.contains("phpstan", ignoreCase = true)) continue

                val psiFile = com.intellij.psi.PsiManager.getInstance(project).findFile(vf) as? NeonFile ?: continue
                collectParametersFromFile(psiFile, variables)
            }
        }

        for (variable in variables) {
            results.addElement(
                LookupElementBuilder.create("$variable%")
                    .withPresentableText("%$variable%")
                    .withIcon(AllIcons.Nodes.Variable)
                    .withTypeText(if (isPhpStan) "phpstan" else "parameter")
                    .withInsertHandler(VariableInsertHandler)
            )
        }
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

    /**
     * Recursively collect parameter keys with dot-notation for nested values.
     */
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

    companion object {
        val NETTE_DEFAULTS = listOf(
            "appDir",
            "wwwDir",
            "tempDir",
            "vendorDir",
            "rootDir",
            "baseUrl",
            "debugMode",
            "productionMode",
            "consoleMode",
        )

        val PHPSTAN_DEFAULTS = listOf(
            "rootDir",
            "currentWorkingDirectory",
            "tmpDir",
        )
    }
}

/**
 * Removes a trailing % after the caret if the completion already inserted one,
 * preventing double %% when completing inside an existing %variable%.
 */
private object VariableInsertHandler : com.intellij.codeInsight.completion.InsertHandler<LookupElement> {
    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        val editor = context.editor
        val offset = editor.caretModel.offset
        val document = editor.document
        if (offset < document.textLength && document.charsSequence[offset] == '%') {
            document.deleteString(offset, offset + 1)
        }
    }
}
