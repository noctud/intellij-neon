package dev.noctud.neon.psi.elements

import com.intellij.psi.PsiElement

/**
 * Parent for other values - can be Scalar or a compound value - array, entity, ...
 */
interface NeonValue : PsiElement, NeonPsiElement
