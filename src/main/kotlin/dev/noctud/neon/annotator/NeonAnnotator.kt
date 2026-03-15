package dev.noctud.neon.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import dev.noctud.neon.lexer.NeonTokenTypes
import dev.noctud.neon.psi.elements.NeonArray
import dev.noctud.neon.psi.elements.NeonKey

class NeonAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element.node
                .elementType === NeonTokenTypes.NEON_INDENT && element.prevSibling != null && element.prevSibling is PsiErrorElement
            && (element.prevSibling as PsiErrorElement).errorDescription == "Tab/space mixing"
        ) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Tab/space mixing")
                .range(element)
                .create()
        } else if (element is NeonArray) {
            val arrayKeys: MutableList<NeonKey?>? = element.keys
            val keys: MutableSet<String?> = HashSet(arrayKeys!!.size)
            for (key in arrayKeys.filterNotNull()) {
                if (keys.contains(key.keyText)) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Duplicate key")
                        .range(key)
                        .create()
                } else {
                    keys.add(key.keyText)
                }
            }
        }
    }
}
