package dev.noctud.neon.psi.elements

/**
 * php-like array
 *
 * it can be a list, hash-map or a mix of those two
 */
interface NeonArray : NeonValue {
    /**
     * Is it a list-like array? i.e. keys are only numeric
     */
    val isList: Boolean

    /**
     * Is it hash-map-like array? I.e. keys are not-numeric
     */
    val isHashMap: Boolean

    /**
     * Get all item values (ignore keys)
     */
    val values: MutableList<NeonValue?>?

    /**
     * Get keys as nodes
     */
    val keys: MutableList<NeonKey?>?

    /**
     * Get all values as a hash-map
     */
    val map: HashMap<String?, NeonValue?>?
}
