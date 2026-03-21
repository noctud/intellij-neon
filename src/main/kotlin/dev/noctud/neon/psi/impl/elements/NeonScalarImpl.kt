package dev.noctud.neon.psi.impl.elements

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.tree.LeafPsiElement
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonScalar
import dev.noctud.neon.reference.NeonScalarReference

class NeonScalarImpl(astNode: ASTNode) : NeonPsiElementImpl(astNode), NeonScalar {
    override val valueText: String
        get() {
            var text = firstChild.text
            if (firstChild is LeafPsiElement && (firstChild as LeafPsiElement).elementType === _NeonTypes.T_STRING) {
                text = text.substring(1, text.length - 1)
            }

            return text
        }

    /**
     * Extract the PHP class FQN from the scalar text, stripping any @ prefix.
     * Returns null if this doesn't look like a class reference.
     */
    val classFqn: String?
        get() {
            val text = valueText
            val stripped = if (text.startsWith("@")) text.substring(1) else text
            val withoutMethod = stripped.substringBefore("::")
            return if (withoutMethod.contains("\\")) withoutMethod else null
        }

    override fun getName(): String {
        return valueText
    }

    override fun getReference(): PsiReference? {
        val fqn = classFqn ?: return null
        val text = valueText
        val prefix = if (text.startsWith("@")) 1 else 0

        // Range covers only the short class name (after last \) so inline rename
        // preserves the namespace prefix
        val lastSep = fqn.lastIndexOf('\\')
        val shortNameStart = prefix + if (lastSep >= 0) lastSep + 1 else 0
        val shortNameEnd = prefix + fqn.length
        return NeonScalarReference(this, TextRange(shortNameStart, shortNameEnd), true, fqn)
    }
}
