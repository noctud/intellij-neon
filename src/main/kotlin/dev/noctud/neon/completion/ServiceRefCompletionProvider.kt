package dev.noctud.neon.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.util.ProcessingContext
import dev.noctud.neon.ext.collectAllServices
import dev.noctud.neon.lexer._NeonTypes

/**
 * Provides autocompletion for @service references.
 * Suggests named services defined in `services:` sections across project neon files.
 */
class ServiceRefCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        results: CompletionResultSet
    ) {
        val element = params.position
        if (element.node.elementType !== _NeonTypes.T_LITERAL) return
        if (!element.text.startsWith("@")) return

        for (service in element.project.collectAllServices()) {
            results.addElement(
                LookupElementBuilder.create(service)
                    .withPresentableText("@$service")
                    .withIcon(AllIcons.Nodes.Plugin)
                    .withTypeText("service")
            )
        }
    }
}
