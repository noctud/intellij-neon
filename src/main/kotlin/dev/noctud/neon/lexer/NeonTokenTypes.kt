package dev.noctud.neon.lexer

import com.google.common.collect.ImmutableMap
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

/**
 * Types of tokens returned form lexer
 *
 * @author Jan Dolecek - juzna.cz@gmail.com
 */
interface NeonTokenTypes {
    companion object {
        // braces
        @JvmField val NEON_LPAREN: IElementType = NeonTokenType("(")
        @JvmField val NEON_RPAREN: IElementType = NeonTokenType(")")
        @JvmField val NEON_LBRACE_CURLY: IElementType = NeonTokenType("{")
        @JvmField val NEON_RBRACE_CURLY: IElementType = NeonTokenType("}")
        @JvmField val NEON_LBRACE_SQUARE: IElementType = NeonTokenType("[")
        @JvmField val NEON_RBRACE_SQUARE: IElementType = NeonTokenType("]")

        // brackets
        @JvmField val closingBrackets: MutableMap<IElementType, IElementType> = ImmutableMap.of<IElementType, IElementType>(
            NEON_LPAREN, NEON_RPAREN,
            NEON_LBRACE_CURLY, NEON_RBRACE_CURLY,
            NEON_LBRACE_SQUARE, NEON_RBRACE_SQUARE
        )

        @JvmField val NEON_STRING: IElementType = NeonTokenType("string")
        @JvmField val NEON_SYMBOL: IElementType = NeonTokenType("symbol") // use a symbol or brace instead (see below)
        @JvmField val NEON_COMMENT: IElementType = NeonTokenType("comment")
        @JvmField val NEON_INDENT: IElementType = NeonTokenType("indent")
        @JvmField val NEON_LITERAL: IElementType = NeonTokenType("literal")
        @JvmField val NEON_KEYWORD: IElementType = NeonTokenType("keyword")
        @JvmField val NEON_WHITESPACE: IElementType = TokenType.WHITE_SPACE // new NeonTokenType("whitespace");
        @JvmField val NEON_UNKNOWN: IElementType = TokenType.BAD_CHARACTER // new NeonTokenType("error");

        // symbols
        @JvmField val NEON_COLON: IElementType = NeonTokenType(":")
        @JvmField val NEON_ASSIGNMENT: IElementType = NeonTokenType("=")
        @JvmField val NEON_ARRAY_BULLET: IElementType = NeonTokenType("-")
        @JvmField val NEON_ITEM_DELIMITER: IElementType = NeonTokenType(",")


        // the rest are deprecated and will be removed
        @JvmField val NEON_IDENTIFIER: IElementType = NeonTokenType("identifier")
        @JvmField val NEON_EOL: IElementType = NeonTokenType("eol")
        @JvmField val NEON_VARIABLE: IElementType = NeonTokenType("variable")
        @JvmField val NEON_NUMBER: IElementType = NeonTokenType("number")
        @JvmField val NEON_REFERENCE: IElementType = NeonTokenType("reference")
        @JvmField val NEON_BLOCK_INHERITENCE: IElementType = NeonTokenType("<")
        @JvmField val NEON_DOUBLE_COLON: IElementType = NeonTokenType("::")
        @JvmField val NEON_DOLLAR: IElementType = NeonTokenType("$")
        @JvmField val NEON_AT: IElementType = NeonTokenType("@")

        // special tokens (identifier in block header or as array key)
        @JvmField val NEON_KEY: IElementType = NeonTokenType("key")

        // sets
        @JvmField val WHITESPACES: TokenSet = TokenSet.create(NEON_WHITESPACE)
        @JvmField val COMMENTS: TokenSet = TokenSet.create(NEON_COMMENT)
        @JvmField val STRING_LITERALS: TokenSet = TokenSet.create(NEON_LITERAL, NEON_STRING)
        @JvmField val ASSIGNMENTS: TokenSet = TokenSet.create(NEON_ASSIGNMENT, NEON_COLON)
        @JvmField val OPEN_BRACKET: TokenSet = TokenSet.create(NEON_LPAREN, NEON_LBRACE_CURLY, NEON_LBRACE_SQUARE)
        @JvmField val CLOSING_BRACKET: TokenSet = TokenSet.create(NEON_RPAREN, NEON_RBRACE_CURLY, NEON_RBRACE_SQUARE)
        @JvmField val SYMBOLS: TokenSet = TokenSet.create(
            NEON_COLON, NEON_ASSIGNMENT, NEON_ARRAY_BULLET, NEON_ITEM_DELIMITER,
            NEON_LPAREN, NEON_RPAREN,
            NEON_LBRACE_CURLY, NEON_RBRACE_CURLY,
            NEON_LBRACE_SQUARE, NEON_RBRACE_SQUARE
        )
    }
}
