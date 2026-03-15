package dev.noctud.neon.parser

import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import dev.noctud.neon.NeonLanguage
import dev.noctud.neon.lexer.NeonTokenTypes

/**
 * Types of elements returned from parser
 */
interface NeonElementTypes {
    companion object {
        @JvmField val FILE: IFileElementType = IFileElementType(NeonLanguage.INSTANCE)
        @JvmField val KEY_VALUE_PAIR: NeonElementType = NeonElementType("Key value pair")
        @JvmField val KEY: NeonElementType = NeonElementType("Key")
        @JvmField val COMPOUND_KEY: NeonElementType = NeonElementType("Compound key")
        @JvmField val HASH: NeonElementType = NeonElementType("Hash")
        @JvmField val ITEM: NeonElementType = NeonElementType("Item")
        @JvmField val ENTITY: NeonElementType = NeonElementType("Entity")
        @JvmField val CHAINED_ENTITY: NeonElementType = NeonElementType("Chained entity")
        @JvmField val ARRAY: NeonElementType = NeonElementType("Array")
        @JvmField val ARGS: NeonElementType = NeonElementType("Args")
        @JvmField val COMPOUND_VALUE: NeonElementType = NeonElementType("Compound value")
        @JvmField val SCALAR_VALUE: NeonElementType = NeonElementType("Scalar value")
        @JvmField val REFERENCE: NeonElementType = NeonElementType("Reference")

        @JvmField val SCALAR_VALUES: TokenSet = TokenSet.create(
            NeonTokenTypes.NEON_STRING,
            NeonTokenTypes.NEON_NUMBER,
            NeonTokenTypes.NEON_REFERENCE,
            NeonTokenTypes.NEON_IDENTIFIER,
            NeonTokenTypes.NEON_LITERAL,
            NeonTokenTypes.NEON_VARIABLE,
            NeonTokenTypes.NEON_DOUBLE_COLON, NeonTokenTypes.NEON_DOLLAR, NeonTokenTypes.NEON_AT
        )
    }
}
