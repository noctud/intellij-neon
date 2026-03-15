package dev.noctud.neon.parser

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.testFramework.ParsingTestCase
import com.intellij.testFramework.TestDataFile
import org.jetbrains.annotations.NonNls
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException

open class ParserTest : ParsingTestCase("", "neon", NeonParserDefinition()) {
    override fun getTestDataPath(): String {
        return "src/test/data/parser"
    }

    protected fun doParserTest(checkResult: Boolean, suppressErrors: Boolean) {
        doTest(true)
        if (!suppressErrors) {
            Assert.assertFalse(
                "PsiFile contains error elements",
                toParseTreeText(myFile, true, includeRanges()).contains("PsiErrorElement")
            )
        }
    }

    @Throws(IOException::class)
    override fun loadFile(@TestDataFile name: @NonNls String): String {
        return FileUtil.loadFile(File(myFullDataPath, name), CharsetToolkit.UTF8, true)
    }

    @Test
    fun testArray1() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArray2() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArray3() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArray4() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArray5() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArray6() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArray7() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArray8() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArray9() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArray10() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArrayComment() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArrayEntity() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArrayEntity2() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArrayInline() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArrayInline2() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArrayInline3() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testReal1() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArrayAfterKey() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testEntityChain() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testEntityArrayValue() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testTabSpaceMixing() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testKeyAfterBullet1() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testKeyAfterBulletArrayAfterKey() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testKeyAfterBullet2() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testKeyAfterBullet3() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testKeyAfterBulletFalse() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArrayNoSpaceColon() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArrayNull() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testArrayIndentedFile() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testItemValueAfterNewLine() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testEmptyLineBeginning() {
        doParserTest(checkResult = true, suppressErrors = false)
    }

    @Test
    fun testErrorInlineArray() {
        doParserTest(checkResult = true, suppressErrors = true)
    }

    @Test
    fun testErrorClosingBracket2() {
        doParserTest(checkResult = true, suppressErrors = true)
    }

    @Test
    fun testErrorClosingBracket() {
        doParserTest(checkResult = true, suppressErrors = true)
    }

    @Test
    fun testErrorTabSpaceMixing() {
        doParserTest(checkResult = true, suppressErrors = true)
    }

    @Test
    fun testErrorIndent() {
        doParserTest(checkResult = true, suppressErrors = true)
    }
}
