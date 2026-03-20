package dev.noctud.neon.lexer

import com.intellij.lexer.Lexer
import com.intellij.lexer.LookAheadLexer
import com.intellij.psi.TokenType

/**
 * Lexer used for syntax highlighting.
 * Reclassifies T_LITERAL tokens based on their content:
 * - Keywords (true/false/null/yes/no/on/off)
 * - Numbers (integers, floats, hex, octal, binary, scientific notation)
 * - Keys (followed by colon or =)
 * - Strings (quoted T_STRING tokens) get string color
 */
class NeonHighlightingLexer(baseLexer: Lexer) : LookAheadLexer(baseLexer, 1) {
    override fun lookAhead(baseLexer: Lexer) {
        val currentToken = baseLexer.tokenType

        if (currentToken === _NeonTypes.T_LITERAL) {
            val text = baseLexer.tokenText ?: ""

            if (KEYWORDS.contains(text)) {
                advanceLexer(baseLexer)
                replaceCachedType(0, NeonTokenTypes.NEON_KEYWORD)
            } else if (isDateTime(text)) {
                advanceLexer(baseLexer)
                replaceCachedType(0, NeonTokenTypes.NEON_DATETIME)
            } else if (isNumeric(text)) {
                advanceLexer(baseLexer)
                replaceCachedType(0, NeonTokenTypes.NEON_NUMBER)
            } else {
                advanceLexer(baseLexer)
                if (!checkKeyAfterLiteral(baseLexer)) {
                    if (text.startsWith("@")) {
                        // Service reference — @routerFactory, @App\Model\Handler
                        replaceCachedType(0, NeonTokenTypes.NEON_SERVICE_REF)
                    } else if (text.contains("\\")) {
                        // PHP class FQN — distinct color
                        replaceCachedType(0, NeonTokenTypes.NEON_CLASSNAME)
                    } else if (isFilePath(text)) {
                        // File path — same color as class references
                        replaceCachedType(0, NeonTokenTypes.NEON_FILEPATH)
                    } else {
                        // Plain string value
                        replaceCachedType(0, NeonTokenTypes.NEON_STRING)
                    }
                }
            }
        } else if (currentToken === _NeonTypes.T_STRING) {
            advanceLexer(baseLexer)
            // Reclassify strings to STRING token for green color
            if (baseLexer.tokenType === TokenType.WHITE_SPACE) {
                advanceLexer(baseLexer)
            }
            if (baseLexer.tokenType === _NeonTypes.T_COLON || baseLexer.tokenType === _NeonTypes.T_ASSIGNMENT) {
                advanceLexer(baseLexer)
                replaceCachedType(0, NeonTokenTypes.NEON_KEY)
            } else {
                replaceCachedType(0, NeonTokenTypes.NEON_STRING)
            }
        } else {
            super.lookAhead(baseLexer)
        }
    }

    private fun checkKeyAfterLiteral(baseLexer: Lexer): Boolean {
        if (baseLexer.tokenType === TokenType.WHITE_SPACE) {
            advanceLexer(baseLexer)
        }
        if (baseLexer.tokenType === _NeonTypes.T_COLON || baseLexer.tokenType === _NeonTypes.T_ASSIGNMENT) {
            advanceLexer(baseLexer)
            replaceCachedType(0, NeonTokenTypes.NEON_KEY)
            return true
        }
        return false
    }

    companion object {
        private val KEYWORDS = setOf(
            "true", "True", "TRUE", "yes", "Yes", "YES", "on", "On", "ON",
            "false", "False", "FALSE", "no", "No", "NO", "off", "Off", "OFF",
            "null", "Null", "NULL"
        )

        // Matches the PHP Neon parser's number classification:
        // decimal integers, floats, scientific notation, hex, octal, binary
        private val NUMERIC = Regex(
            "^-?(?:" +
                "0x[0-9a-fA-F]+" +       // hex
                "|0o[0-7]+" +             // octal
                "|0b[01]+" +              // binary
                "|[0-9]*\\.?[0-9]+(?:[eE][+-]?[0-9]+)?" + // decimal/float/scientific
            ")$"
        )

        fun isNumeric(text: String): Boolean {
            return NUMERIC.matches(text)
        }

        // Matches the PHP Neon parser's datetime pattern exactly:
        // YYYY-MM-DD with optional time, fractional seconds, and timezone
        private val DATETIME = Regex(
            "^\\d{4}-\\d{1,2}-\\d{1,2}" +
            "(?:[Tt ]\\d{1,2}:\\d{2}:\\d{2}(?:\\.\\d+)?" +
            "\\s*(?:Z|[-+]\\d{1,2}:?\\d{0,2})?)?\$"
        )

        fun isDateTime(text: String): Boolean {
            return DATETIME.matches(text)
        }

        private val FILE_PATH = Regex(".+[/\\\\].+\\.(?:php|neon)$")

        fun isFilePath(text: String): Boolean {
            return FILE_PATH.matches(text)
        }
    }
}
