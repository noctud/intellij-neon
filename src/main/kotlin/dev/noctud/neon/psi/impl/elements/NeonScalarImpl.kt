package dev.noctud.neon.psi.impl.elements

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.tree.LeafPsiElement
import dev.noctud.neon.lexer.NeonTokenTypes
import dev.noctud.neon.psi.elements.NeonScalar
import dev.noctud.neon.reference.NeonScalarReference

class NeonScalarImpl(astNode: ASTNode) : NeonPsiElementImpl(astNode), NeonScalar {
    override fun toString(): String {
        return "Neon scalar"
    }

    override val valueText: String
        get() {
            var text = firstChild.text
            if (firstChild is LeafPsiElement && (firstChild as LeafPsiElement).elementType === NeonTokenTypes.NEON_STRING) {
                text = text.substring(1, text.length - 1)
            }

            return text
        }

    override fun getName(): String {
        return valueText
    }

    /*
    TODO: Fix rename
    override fun getReference(): PsiReference {
        return NeonScalarReference(this, com.intellij.openapi.util.TextRange(0, textLength), true, valueText)
    }*/
}
