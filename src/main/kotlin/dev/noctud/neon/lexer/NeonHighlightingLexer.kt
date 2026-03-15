package dev.noctud.neon.lexer

import com.intellij.lexer.Lexer
import com.intellij.lexer.LookAheadLexer

/**
 * Lexer used for syntax highlighting
 *
 * It reuses the simple lexer, changing types of some tokens
 */
class NeonHighlightingLexer(baseLexer: Lexer) : LookAheadLexer(baseLexer, 1) {
    override fun lookAhead(baseLexer: Lexer) {
        val currentToken = baseLexer.tokenType

        if (currentToken === NeonTokenTypes.NEON_LITERAL && KEYWORDS.contains(baseLexer.tokenText)) {
            advanceLexer(baseLexer)
            replaceCachedType(0, NeonTokenTypes.NEON_KEYWORD)
        } else if (currentToken === NeonTokenTypes.NEON_LITERAL || currentToken === NeonTokenTypes.NEON_STRING) {
            advanceLexer(baseLexer)

            if (baseLexer.tokenType === NeonTokenTypes.NEON_WHITESPACE) {
                advanceLexer(baseLexer)
            }

            if (baseLexer.tokenType === NeonTokenTypes.NEON_COLON) {
                advanceLexer(baseLexer)
                replaceCachedType(0, NeonTokenTypes.NEON_KEY)
            }
        } else {
            super.lookAhead(baseLexer)
        }
    }

    companion object {
        private val KEYWORDS: MutableSet<String?> = HashSet<String?>(
            mutableListOf<String?>(
                "true", "True", "TRUE", "yes", "Yes", "YES", "on", "On", "ON",
                "false", "False", "FALSE", "no", "No", "NO", "off", "Off", "OFF",
                "null", "Null", "NULL"
            )
        )
    }
}
