package dev.noctud.neon.lexer

import com.intellij.lexer.MergingLexerAdapter
import org.junit.Test
import dev.noctud.neon.BaseLexerTestCase

class LexerTest : BaseLexerTestCase("src/test/data/parser/") {
    override fun createLexer(): MergingLexerAdapter {
        return NeonLexer()
    }
    
    @Test
    @Throws(Exception::class)
    fun testSimple() {
        doTest(
            "name: 'Jan'", arrayOf(
                Pair(NeonTokenTypes.NEON_LITERAL, "name"),
                Pair(NeonTokenTypes.NEON_COLON, ":"),
                Pair(NeonTokenTypes.NEON_WHITESPACE, " "),
                Pair(NeonTokenTypes.NEON_STRING, "'Jan'"),
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun testTabAfterKey() {
        doTest(
            "name: \t'Jan'\nsurname:\t \t 'Dolecek'", arrayOf(
                Pair(NeonTokenTypes.NEON_LITERAL, "name"),
                Pair(NeonTokenTypes.NEON_COLON, ":"),
                Pair(NeonTokenTypes.NEON_WHITESPACE, " \t"),
                Pair(NeonTokenTypes.NEON_STRING, "'Jan'"),
                Pair(NeonTokenTypes.NEON_INDENT, "\n"),
                Pair(NeonTokenTypes.NEON_LITERAL, "surname"),
                Pair(NeonTokenTypes.NEON_COLON, ":"),
                Pair(NeonTokenTypes.NEON_WHITESPACE, "\t \t "),
                Pair(NeonTokenTypes.NEON_STRING, "'Dolecek'"),
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun testDefault() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testArray1() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testArray2() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testArray3() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testArray4() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testArray5() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testArray6() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testArray10() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testArrayEntity() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testArrayIndentedFile() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testArrayNoSpaceColon() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testMultiline1() {
        doTestFromFile()
    }
}
