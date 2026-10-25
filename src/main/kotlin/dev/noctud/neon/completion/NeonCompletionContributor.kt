package dev.noctud.neon.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement
import dev.noctud.neon.ext.isTranslation

class NeonCompletionContributor : CompletionContributor() {
    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        // Every provider below completes config (keys, classes, services, parameters), a catalogue holds prose
        if (parameters.originalFile.isTranslation()) return
        super.fillCompletionVariants(parameters, result)
    }

    init {
        extend(
            CompletionType.BASIC,
            StandardPatterns.instanceOf<PsiElement?>(PsiElement::class.java),
            KeywordCompletionProvider()
        )
        extend(
            CompletionType.BASIC,
            StandardPatterns.instanceOf<PsiElement?>(PsiElement::class.java),
            ServiceCompletionProvider()
        )
        extend(
            CompletionType.BASIC,
            StandardPatterns.instanceOf<PsiElement?>(PsiElement::class.java),
            ClassCompletionProvider()
        )
        extend(
            CompletionType.BASIC,
            StandardPatterns.instanceOf<PsiElement?>(PsiElement::class.java),
            ParameterCompletionProvider()
        )
        extend(
            CompletionType.BASIC,
            StandardPatterns.instanceOf<PsiElement?>(PsiElement::class.java),
            PhpStanIdentifierCompletionProvider()
        )
        extend(
            CompletionType.BASIC,
            StandardPatterns.instanceOf<PsiElement?>(PsiElement::class.java),
            ServiceRefCompletionProvider()
        )
    }
}
