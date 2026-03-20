package dev.noctud.neon.lexer

import com.intellij.lexer.Lexer
import com.intellij.psi.TokenType
import com.intellij.testFramework.UsefulTestCase
import junit.framework.TestCase
import org.junit.Test

class NeonHighlightingLexerTest : UsefulTestCase() {
    @Test
    fun testKeys() {
        val l: Lexer = NeonHighlightingLexer(NeonLexer())
        l.start("key: val")

        assertEquals(NeonTokenTypes.NEON_KEY, l.tokenType) // this is important
        TestCase.assertEquals(0, l.tokenStart)
        TestCase.assertEquals(3, l.tokenEnd)
        TestCase.assertEquals("key", l.tokenText)
        l.advance()

        assertEquals(_NeonTypes.T_COLON, l.tokenType)
        TestCase.assertEquals(3, l.tokenStart)
        TestCase.assertEquals(4, l.tokenEnd)
        TestCase.assertEquals(":", l.tokenText)
        l.advance()

        assertEquals(TokenType.WHITE_SPACE, l.tokenType)
        TestCase.assertEquals(4, l.tokenStart)
        TestCase.assertEquals(5, l.tokenEnd)
        TestCase.assertEquals(" ", l.tokenText)
        l.advance()

        assertEquals(_NeonTypes.T_LITERAL, l.tokenType)
        TestCase.assertEquals(5, l.tokenStart)
        TestCase.assertEquals(8, l.tokenEnd)
        TestCase.assertEquals("val", l.tokenText)
        l.advance()

        assertEquals(null, l.tokenType)
    }

    @Test
    fun testKeywords() {
        val l: Lexer = NeonHighlightingLexer(NeonLexer())
        l.start("[true,off,TruE,\"true\"]")

        assertEquals(_NeonTypes.T_LBRACE_SQUARE, l.tokenType)
        TestCase.assertEquals(0, l.tokenStart)
        TestCase.assertEquals(1, l.tokenEnd)
        TestCase.assertEquals("[", l.tokenText)
        l.advance()

        assertEquals(NeonTokenTypes.NEON_KEYWORD, l.tokenType)
        TestCase.assertEquals(1, l.tokenStart)
        TestCase.assertEquals(5, l.tokenEnd)
        TestCase.assertEquals("true", l.tokenText)
        l.advance()

        assertEquals(_NeonTypes.T_ITEM_DELIMITER, l.tokenType)
        TestCase.assertEquals(5, l.tokenStart)
        TestCase.assertEquals(6, l.tokenEnd)
        TestCase.assertEquals(",", l.tokenText)
        l.advance()

        assertEquals(NeonTokenTypes.NEON_KEYWORD, l.tokenType)
        TestCase.assertEquals(6, l.tokenStart)
        TestCase.assertEquals(9, l.tokenEnd)
        TestCase.assertEquals("off", l.tokenText)
        l.advance()

        assertEquals(_NeonTypes.T_ITEM_DELIMITER, l.tokenType)
        TestCase.assertEquals(9, l.tokenStart)
        TestCase.assertEquals(10, l.tokenEnd)
        TestCase.assertEquals(",", l.tokenText)
        l.advance()

        assertEquals(_NeonTypes.T_LITERAL, l.tokenType)
        TestCase.assertEquals(10, l.tokenStart)
        TestCase.assertEquals(14, l.tokenEnd)
        TestCase.assertEquals("TruE", l.tokenText)
        l.advance()

        assertEquals(_NeonTypes.T_ITEM_DELIMITER, l.tokenType)
        TestCase.assertEquals(14, l.tokenStart)
        TestCase.assertEquals(15, l.tokenEnd)
        TestCase.assertEquals(",", l.tokenText)
        l.advance()

        assertEquals(_NeonTypes.T_STRING, l.tokenType)
        TestCase.assertEquals(15, l.tokenStart)
        TestCase.assertEquals(21, l.tokenEnd)
        TestCase.assertEquals("\"true\"", l.tokenText)
        l.advance()

        assertEquals(_NeonTypes.T_RBRACE_SQUARE, l.tokenType)
        TestCase.assertEquals(21, l.tokenStart)
        TestCase.assertEquals(22, l.tokenEnd)
        TestCase.assertEquals("]", l.tokenText)
        l.advance()

        assertEquals(null, l.tokenType)
    }
}
