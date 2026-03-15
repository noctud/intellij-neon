package dev.noctud.neon.psi.elements

interface NeonChainedEntity : NeonValue {
    val values: MutableList<NeonEntity?>?
}
