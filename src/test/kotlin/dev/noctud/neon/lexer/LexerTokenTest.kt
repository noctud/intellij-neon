package dev.noctud.neon.lexer

import com.intellij.lexer.MergingLexerAdapter
import org.junit.Test
import dev.noctud.neon.BaseLexerTestCase

class LexerTokenTest : BaseLexerTestCase("data/psi/neonPsi/") {
    override fun createLexer(): MergingLexerAdapter {
        return NeonLexer()
    }

    @Test
    @Throws(Exception::class)
    fun testDefault() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testDefault1() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testDefault2() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testNested() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testNested1() {
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
    fun testArray7() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testArray8() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testArray9() {
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
    fun testMultiline() {
        doTestFromFile()
    }

    @Test
    @Throws(Exception::class)
    fun testItemValueAfterNewLine() {
        doTestFromFile()
    }
}
