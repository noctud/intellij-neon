package dev.noctud.neon.editor

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.psi.PsiElement
import dev.noctud.neon.psi.elements.NeonArray
import dev.noctud.neon.psi.elements.NeonFile
import dev.noctud.neon.psi.elements.NeonKeyValPair
import dev.noctud.neon.psi.elements.NeonScalar

class NeonStructureViewElement(element: PsiElement?) : PsiTreeElementBase<PsiElement?>(element) {
    override fun getChildrenBase(): MutableCollection<StructureViewTreeElement?> {
        val elements: MutableList<StructureViewTreeElement?> = ArrayList()
        val element = getElement()

        if (element is NeonFile) {
            val value = element.value
            if (value is NeonArray) { // top level array -> show it's elements
                addArrayElements(elements, value)
            } else if (value !is NeonScalar) {
                // file children directly
                addArrayElements(elements, element)
            }
        } else if (element is NeonKeyValPair && element.value is NeonArray) {
            addArrayElements(elements, element.value ?: return elements)
        } else if (element is NeonArray) {
            addArrayElements(elements, element)
        }

        return elements
    }

    private fun addArrayElements(elements: MutableList<StructureViewTreeElement?>, firstValue: PsiElement) {
        for (child in firstValue.children) {
            elements.add(NeonStructureViewElement(child))
        }
    }

    override fun getPresentableText(): String? {
        val element = getElement()
        return if (element is NeonFile) {
            element.getName()
        } else if (element is NeonArray) {
            "array"
        } else if (element is NeonKeyValPair) {
            element.keyText
        } else {
            "?"
        }
    }
}
