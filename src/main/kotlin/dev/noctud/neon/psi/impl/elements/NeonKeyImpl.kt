package dev.noctud.neon.psi.impl.elements

import com.intellij.lang.ASTNode
import com.intellij.psi.tree.TokenSet
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonKey

class NeonKeyImpl(astNode: ASTNode) : NeonPsiElementImpl(astNode), NeonKey {
    override val keyText: String
        get() {
            // KEY contains scalar + colon/assignment. Extract just the scalar text.
            val scalarNodes = node.getChildren(SCALAR_SET)
            if (scalarNodes.isNotEmpty()) {
                return scalarNodes[0].text
            }
            // Fallback for bullet keys
            val bulletNodes = node.getChildren(BULLET_SET)
            if (bulletNodes.isNotEmpty()) {
                return bulletNodes[0].text
            }
            return node.text
        }

    override fun getName(): String {
        return this.keyText
    }

    companion object {
        private val SCALAR_SET = TokenSet.create(_NeonTypes.SCALAR)
        private val BULLET_SET = TokenSet.create(_NeonTypes.T_ARRAY_BULLET)
    }
}
