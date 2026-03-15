package dev.noctud.neon.completion

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import dev.noctud.neon.lexer.NeonTokenTypes
import dev.noctud.neon.parser.NeonElementTypes
import dev.noctud.neon.parser.NeonParser
import dev.noctud.neon.psi.elements.NeonArray
import dev.noctud.neon.psi.elements.NeonFile
import dev.noctud.neon.psi.elements.NeonKeyValPair
import dev.noctud.neon.psi.elements.NeonScalar

object CompletionUtil {
    fun isIncompleteKey(el: PsiElement): Boolean {
        if (!NeonTokenTypes.STRING_LITERALS.contains(el.node.elementType)) {
            return false
        }

        //first scalar in file
        if (el.parent is NeonScalar && el.parent.parent is NeonFile) {
            return true
        }

        //error element
        if (el.parent is NeonArray
            && el.prevSibling is PsiErrorElement
            && (el.prevSibling as PsiErrorElement).errorDescription == NeonParser.EXPECTED_ARRAY_ITEM
        ) {
            return true
        }

        //new key after new line
        return el.parent is NeonScalar &&
            (el.parent.parent is NeonKeyValPair || el.parent.parent.node.elementType == NeonElementTypes.ITEM) &&
            el.parent.prevSibling.node.elementType == NeonTokenTypes.NEON_INDENT
    }
}
