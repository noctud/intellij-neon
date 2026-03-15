package dev.noctud.neon.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.completion.PhpCompletionUtil
import com.jetbrains.php.completion.insert.PhpNamespaceInsertHandler
import dev.noctud.neon.psi.elements.NeonEntity
import dev.noctud.neon.psi.elements.NeonScalar

class NamespaceCompletionProvider : CompletionProvider<CompletionParameters?>() {
    override fun addCompletions(params: CompletionParameters, context: ProcessingContext, result: CompletionResultSet) {
        val curr = params.position.originalElement
        if (curr.parent !is NeonEntity && curr.parent !is NeonScalar) return
        val phpIndex = PhpIndex.getInstance(curr.project)
        var prefix = result.prefixMatcher.prefix
        var namespace = ""

        if (prefix.contains("\\")) {
            val index = prefix.lastIndexOf("\\")
            namespace = prefix.substring(0, index) + "\\"
            prefix = prefix.substring(index + 1)
        }

        PhpCompletionUtil.addSubNamespaces(
            namespace,
            result.withPrefixMatcher(prefix),
            phpIndex,
            PhpNamespaceInsertHandler.getInstance()
        )
    }
}
