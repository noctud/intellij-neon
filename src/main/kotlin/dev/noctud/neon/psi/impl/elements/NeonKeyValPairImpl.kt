package dev.noctud.neon.psi.impl.elements

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.NonNls
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonKey
import dev.noctud.neon.psi.elements.NeonKeyValPair
import dev.noctud.neon.psi.elements.NeonValue

class NeonKeyValPairImpl(astNode: ASTNode) : NeonPsiElementImpl(astNode), NeonKeyValPair {
    override val key: NeonKey?
        get() {
            val keys = node.getChildren(KEY_SET)
            return if (keys.isNotEmpty()) keys[0].psi as NeonKey? else null
        }

    override val keyText: String?
        get() = this.key?.keyText

    override val value: NeonValue?
        get() {
            if (lastChild is NeonValue) {
                return lastChild as NeonValue
            }
            return null
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
        private val KEY_SET = com.intellij.psi.tree.TokenSet.create(_NeonTypes.KEY)
    }
}
