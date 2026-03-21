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
import dev.noctud.neon.ext.EnvFileParser
import dev.noctud.neon.ext.collectAllParameters
import dev.noctud.neon.ext.collectParameters
import dev.noctud.neon.ext.isPhpStan
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonFile

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

        val file = element.containingFile ?: return
        val isPhpStan = file.isPhpStan()

        val variables = mutableSetOf<String>()
        val envVariables = mutableSetOf<String>()

        if (isPhpStan) {
            variables.addAll(PHPSTAN_DEFAULTS)
            (element.containingFile as? NeonFile)?.collectParameters(variables)
        } else {
            variables.addAll(NETTE_DEFAULTS)
            variables.addAll(element.project.collectAllParameters())
            envVariables.addAll(EnvFileParser.collectEnvVariables(element.project) - variables)
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

        for (variable in envVariables) {
            results.addElement(
                LookupElementBuilder.create("$variable%")
                    .withPresentableText("%$variable%")
                    .withIcon(AllIcons.Nodes.Variable)
                    .withTypeText(".env")
                    .withInsertHandler(VariableInsertHandler)
            )
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
