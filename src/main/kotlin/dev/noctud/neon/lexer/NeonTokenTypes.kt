package dev.noctud.neon.lexer

import com.intellij.psi.tree.IElementType

/**
 * Additional token types used by the highlighting lexer.
 * These are NOT produced by the JFlex lexer — they are created by
 * NeonHighlightingLexer which reclassifies base tokens for syntax coloring.
 *
 * Base lexer tokens are defined in the generated _NeonTypes class.
 */
object NeonTokenTypes {
    @JvmField val NEON_KEYWORD: IElementType = NeonTokenType("keyword")
    @JvmField val NEON_KEY: IElementType = NeonTokenType("key")
    @JvmField val NEON_NUMBER: IElementType = NeonTokenType("number")
    @JvmField val NEON_VARIABLE: IElementType = NeonTokenType("variable")
}
