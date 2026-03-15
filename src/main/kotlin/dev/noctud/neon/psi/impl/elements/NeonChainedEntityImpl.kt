package dev.noctud.neon.psi.impl.elements

import com.intellij.lang.ASTNode
import dev.noctud.neon.parser.NeonElementTypes
import dev.noctud.neon.psi.elements.NeonChainedEntity
import dev.noctud.neon.psi.elements.NeonEntity

class NeonChainedEntityImpl(astNode: ASTNode) : NeonPsiElementImpl(astNode), NeonChainedEntity {
    override fun toString(): String {
        return "Neon chained entity"
    }

    override val values: MutableList<NeonEntity?>?
        get() = findChildrenByType<NeonEntity?>(NeonElementTypes.ENTITY)
}
