package dev.noctud.neon.editor

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import dev.noctud.neon.lexer.NeonHighlightingLexer
import dev.noctud.neon.lexer.NeonLexer
import dev.noctud.neon.lexer.NeonTokenTypes
import dev.noctud.neon.lexer._NeonTypes

class NeonSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer {
        return NeonHighlightingLexer(NeonLexer())
    }

    override fun getTokenHighlights(type: IElementType?): Array<TextAttributesKey> {
        return pack(ATTRIBUTES[type])
    }

    companion object {
        const val UNKNOWN_ID: String = "Bad character"
        val UNKNOWN: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey(UNKNOWN_ID, HighlighterColors.BAD_CHARACTER)

        const val COMMENT_ID: String = "Comment"
        val COMMENT: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey(COMMENT_ID, DefaultLanguageHighlighterColors.LINE_COMMENT)

        const val IDENTIFIER_ID: String = "Identifier"
        val IDENTIFIER: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey(IDENTIFIER_ID, DefaultLanguageHighlighterColors.KEYWORD)

        const val INTERPUNCTION_ID: String = "Interpunction"
        val INTERPUNCTION: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey(INTERPUNCTION_ID, DefaultLanguageHighlighterColors.DOT)

        const val NUMBER_ID: String = "Number"
        val NUMBER: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey(NUMBER_ID, DefaultLanguageHighlighterColors.NUMBER)

        const val KEYWORD_ID: String = "Keyword"
        val KEYWORD: TextAttributesKey =
            TextAttributesKey.createTextAttributesKey(KEYWORD_ID, DefaultLanguageHighlighterColors.KEYWORD)

        // Groups of IElementType's
        val sBAD: TokenSet = TokenSet.create(TokenType.BAD_CHARACTER, _NeonTypes.T_UNKNOWN)
        val sCOMMENTS: TokenSet = TokenSet.create(_NeonTypes.T_COMMENT)
        val sIDENTIFIERS: TokenSet = TokenSet.create(NeonTokenTypes.NEON_KEY)
        val sINTERPUNCTION: TokenSet = TokenSet.create(
            _NeonTypes.T_LPAREN,
            _NeonTypes.T_RPAREN,
            _NeonTypes.T_LBRACE_CURLY,
            _NeonTypes.T_RBRACE_CURLY,
            _NeonTypes.T_LBRACE_SQUARE,
            _NeonTypes.T_RBRACE_SQUARE,
            _NeonTypes.T_ITEM_DELIMITER,
            _NeonTypes.T_ASSIGNMENT
        )
        val sNUMBERS: TokenSet = TokenSet.create(NeonTokenTypes.NEON_NUMBER)
        val sKEYWORDS: TokenSet = TokenSet.create(NeonTokenTypes.NEON_KEYWORD)


        // Static container
        private val ATTRIBUTES: MutableMap<IElementType?, TextAttributesKey?> =
            HashMap<IElementType?, TextAttributesKey?>()


        // Fill in the map
        init {
            fillMap(ATTRIBUTES, sBAD, UNKNOWN)
            fillMap(ATTRIBUTES, sCOMMENTS, COMMENT)
            fillMap(ATTRIBUTES, sIDENTIFIERS, IDENTIFIER)
            fillMap(ATTRIBUTES, sINTERPUNCTION, INTERPUNCTION)
            fillMap(ATTRIBUTES, sNUMBERS, NUMBER)
            fillMap(ATTRIBUTES, sKEYWORDS, KEYWORD)
        }
    }
}
