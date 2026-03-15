package dev.noctud.neon.psi.impl.elements

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.NonNls
import dev.noctud.neon.parser.NeonElementTypes
import dev.noctud.neon.psi.elements.NeonArray
import dev.noctud.neon.psi.elements.NeonEntity

class NeonEntityImpl(astNode: ASTNode) : NeonPsiElementImpl(astNode), NeonEntity {
    override fun toString(): String {
        return "Neon entity"
    }

    override fun getName(): String? {
        return node.firstChildNode.psi.text
    }

    override val args: NeonArray?
        get() {
            val children: Array<ASTNode?> =
                node.getChildren(TokenSet.create(NeonElementTypes.ARRAY))
            return if (children.isNotEmpty()) {
                children[0]!!.psi as NeonArray? // should be hash I guess
            } else {
                null
            }
        }

    @Throws(IncorrectOperationException::class)
    override fun setName(s: @NonNls String): PsiElement? {
        // TODO: for refactoring
        return null
    }
}
