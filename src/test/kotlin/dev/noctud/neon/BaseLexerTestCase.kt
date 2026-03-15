package dev.noctud.neon

import com.intellij.lexer.Lexer
import com.intellij.lexer.MergingLexerAdapter
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.UsefulTestCase
import junit.framework.TestCase
import org.jetbrains.annotations.NonNls
import java.io.File
import java.io.IOException

abstract class BaseLexerTestCase protected constructor(private val resourceDataPath: String) : UsefulTestCase() {
    protected abstract fun createLexer(): MergingLexerAdapter

    /**
     * Test that lexing a given piece of code will give particular tokens
     *
     * @param text Sample piece of neon code
     * @param expectedTokens List of tokens expected from lexer
     */
    protected fun doTest(text: @NonNls String, expectedTokens: Array<Pair<IElementType?, String?>>) {
        val lexer: Lexer = createLexer()
        doTest(text, expectedTokens, lexer)
    }

    private fun doTest(text: String, expectedTokens: Array<Pair<IElementType?, String?>>, lexer: Lexer) {
        lexer.start(text)
        var idx = 0
        while (lexer.tokenType != null) {
            if (idx >= expectedTokens.size) fail("Too many tokens from lexer; unexpected " + lexer.tokenType)

            val expected = expectedTokens[idx++]
            assertEquals("Wrong token", expected.first, lexer.tokenType)

            val tokenText = lexer.bufferSequence.subSequence(lexer.tokenStart, lexer.tokenEnd).toString()
            TestCase.assertEquals(expected.second, tokenText)
            lexer.advance()
        }

        if (idx < expectedTokens.size) fail("Not enough tokens from lexer, expected " + expectedTokens.size + " but got only " + idx)
    }

    @Throws(Exception::class)
    fun doTestFromFile() {
        val code = doLoadFile(resourceDataPath, getTestName(false) + ".neon")

        val lexer: Lexer = createLexer()
        val sb = StringBuilder()

        lexer.start(code)
        while (lexer.tokenType != null) {
            sb.append(lexer.tokenType.toString())
            sb.append("\n")
            lexer.advance()
        }

        //		System.out.println(sb);

        // Match to original
        val lexed = doLoadFile(resourceDataPath, getTestName(false) + ".lexed")
        TestCase.assertEquals(lexed.lowercase(), sb.toString().lowercase())
    }

    @Throws(IOException::class)
    private fun doLoadFile(myResourcePath: String?, name: String): String {
        val path = (myResourcePath ?: "") + name

        // 1) Try to load from test resources on classpath
        val cl = javaClass.classLoader
        val url = cl.getResource(path)
        if (url != null) {
            var text = FileUtil.loadFile(File(url.file), CharsetToolkit.UTF8)
            text = StringUtil.convertLineSeparators(text)
            return text
        }

        // 2) Fallback: load directly from the filesystem (useful when files are under src/test/data)
        val file = File(path)
        if (file.exists()) {
            var text = FileUtil.loadFile(file, CharsetToolkit.UTF8)
            text = StringUtil.convertLineSeparators(text)
            return text
        }

        throw AssertionError("Source file $path not found")
    }
}
