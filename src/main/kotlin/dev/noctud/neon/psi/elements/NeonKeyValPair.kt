package dev.noctud.neon.psi.elements

import com.intellij.psi.PsiNamedElement

interface NeonKeyValPair : PsiNamedElement, NeonPsiElement {
    val key: NeonKey?

    val keyText: String?

    val value: NeonValue?
}
