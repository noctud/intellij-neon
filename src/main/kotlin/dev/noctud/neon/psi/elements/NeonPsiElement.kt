package dev.noctud.neon.psi.elements

import com.intellij.psi.NavigatablePsiElement

/**
 * parent for all elements, or those which don't need a special class
 */
interface NeonPsiElement : NavigatablePsiElement {
    val neonFile: NeonFile?
}
