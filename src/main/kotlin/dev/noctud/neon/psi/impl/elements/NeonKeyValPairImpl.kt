package dev.noctud.neon.psi.impl.elements

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.NonNls
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonKey
import dev.noctud.neon.psi.elements.NeonKeyValPair
import dev.noctud.neon.psi.elements.NeonValue

class NeonKeyValPairImpl(astNode: ASTNode) : NeonPsiElementImpl(astNode), NeonKeyValPair {
    override val key: NeonKey?
        get() {
            // Direct KEY child (for NAMED_KEY_VAL_PAIR and BULLET_KEY_VAL_PAIR)
            val keys = node.getChildren(KEY_SET)
            if (keys.isNotEmpty()) return keys[0].psi as NeonKey?

            // Wrapper KEY_VAL_PAIR delegates to inner NAMED/BULLET child
            val inner = innerKeyValPair
            return inner?.key
        }

    override val keyText: String?
        get() = this.key?.keyText

    override val value: NeonValue?
        get() {
            if (lastChild is NeonValue) {
                return lastChild as NeonValue
            }
            // Wrapper KEY_VAL_PAIR delegates to inner child
            val inner = innerKeyValPair
            return inner?.value
        }

    /**
     * For the outer KEY_VAL_PAIR wrapper, get the inner NAMED_KEY_VAL_PAIR or BULLET_KEY_VAL_PAIR.
     */
    private val innerKeyValPair: NeonKeyValPair?
        get() {
            val children = node.getChildren(INNER_KVP_SET)
            return if (children.isNotEmpty()) children[0].psi as? NeonKeyValPair else null
        }

    @Throws(IncorrectOperationException::class)
    override fun setName(s: @NonNls String): PsiElement? {
        // TODO: needed for refactoring
        return null
    }

    override fun getName(): String? {
        return this.keyText
    }

    companion object {
        private val KEY_SET = TokenSet.create(_NeonTypes.KEY)
        private val INNER_KVP_SET = TokenSet.create(_NeonTypes.NAMED_KEY_VAL_PAIR, _NeonTypes.BULLET_KEY_VAL_PAIR)
    }
}
