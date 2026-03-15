package dev.noctud.neon.psi.elements

import com.intellij.psi.PsiNamedElement

/**
 * Entity - identifier with arguments
 */
interface NeonEntity : NeonValue, PsiNamedElement {
    val args: NeonArray?
}
