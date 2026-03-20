package dev.noctud.neon.psi.impl.elements

import com.intellij.lang.ASTNode
import dev.noctud.neon.psi.elements.NeonChainedEntity
import dev.noctud.neon.psi.elements.NeonEntity

class NeonChainedEntityImpl(astNode: ASTNode) : NeonPsiElementImpl(astNode), NeonChainedEntity {
    // Entity and chained entity rules are not yet in the BNF grammar (TODO)
    // When added, this will find entity children properly
    override val values: MutableList<NeonEntity?>?
        get() = null
}
