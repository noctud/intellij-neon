package dev.noctud.neon.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement

class NeonCompletionContributor : CompletionContributor() {
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
    }
}
