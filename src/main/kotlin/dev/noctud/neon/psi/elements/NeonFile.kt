package dev.noctud.neon.psi.elements

import com.intellij.psi.PsiFile

interface NeonFile : PsiFile, NeonPsiElement {
    val value: NeonPsiElement?
}
