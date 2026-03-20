package dev.noctud.neon.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonArray

class NeonAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is PsiErrorElement) {
            // Check if the parser error is actually caused by tab/space mixing
            // and provide a clearer error message
            val prevSibling = element.prevSibling ?: return
            if (prevSibling.node.elementType === _NeonTypes.T_INDENT) {
                val indentText = prevSibling.text.replace("\n", "")
                if (indentText.isNotEmpty() && fileHasDifferentIndentBase(element, indentText[0])) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Invalid combination of tabs and spaces")
                        .range(element)
                        .create()
                }
            }
        } else if (element is NeonArray) {
            val arrayKeys = element.keys ?: return
            val keys: MutableSet<String?> = HashSet(arrayKeys.size)
            for (key in arrayKeys.filterNotNull()) {
                if (key.keyText == "-" || key.keyText?.startsWith("- ") == true) continue
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

    /**
     * Check if the file contains any indent that uses a different base whitespace
     * character than the given one. Scans all indents in the file.
     */
    private fun fileHasDifferentIndentBase(element: PsiElement, indentChar: Char): Boolean {
        val fileText = element.containingFile?.text ?: return false
        var i = 0
        while (i < fileText.length) {
            if (fileText[i] == '\n' && i + 1 < fileText.length) {
                val nextChar = fileText[i + 1]
                if ((nextChar == '\t' || nextChar == ' ') && nextChar != indentChar) {
                    return true
                }
            }
            i++
        }
        return false
    }
}
