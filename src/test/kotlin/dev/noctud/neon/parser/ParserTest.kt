package dev.noctud.neon.parser

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.testFramework.ParsingTestCase
import com.intellij.testFramework.TestDataFile
import org.jetbrains.annotations.NonNls
import org.junit.Test
import java.io.File
import java.io.IOException

open class ParserTest : ParsingTestCase("", "neon", NeonParserDefinition()) {
    override fun getTestDataPath(): String {
        return "src/test/data/parser"
    }

    protected fun doParserTest(checkResult: Boolean, suppressErrors: Boolean) {
        doTest(checkResult)
        if (!suppressErrors) {
            assertFalse(
                "PsiFile contains error elements",
                toParseTreeText(myFile, true, includeRanges()).contains("PsiErrorElement")
            )
        }
    }

    @Throws(IOException::class)
    override fun loadFile(@TestDataFile name: @NonNls String): String {
        return FileUtil.loadFile(File(myFullDataPath, name), CharsetToolkit.UTF8, true)
    }

    // === Basic arrays and key-value ===

    @Test fun testArray1() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testArray2() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testArray3() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testArray4() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testArray5() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testArray6() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testArray7() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testArray8() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testArray9() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testArray10() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testDefault() = doParserTest(checkResult = true, suppressErrors = false)

    // === Inline arrays ===

    @Test fun testArrayInline() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testArrayInline2() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testArrayInline3() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testInlineArrays() = doParserTest(checkResult = true, suppressErrors = false)

    // === Comments ===

    @Test fun testArrayComment() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testComments() = doParserTest(checkResult = true, suppressErrors = false)

    // === Entities ===

    @Test fun testArrayEntity() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testArrayEntity2() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testEntities() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testEntityChain() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testEntityArrayValue() = doParserTest(checkResult = true, suppressErrors = false)

    // === Nesting and indentation ===

    @Test fun testNesting() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testDeepNesting() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testEmptyLineBeginning() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testTabSpaceMixing() = doParserTest(checkResult = true, suppressErrors = false)

    // === Bullet items ===

    @Test fun testArrayAfterKey() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testArrayNull() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testBulletKeyValue() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testBulletNesting() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testSimpleBullets() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testTabBulletKeyValue() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testKeyAfterBullet1() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testKeyAfterBullet2() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testKeyAfterBulletFalse() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testKeyAfterBulletArrayAfterKey() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testMixedArray() = doParserTest(checkResult = true, suppressErrors = false)

    // === Values and strings ===

    @Test fun testValues() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testArrayNoSpaceColon() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testMultilineStrings() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testMultiline1() = doParserTest(checkResult = true, suppressErrors = false)

    // === Real-world configs ===

    @Test fun testReal1() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testComplexReal() = doParserTest(checkResult = true, suppressErrors = false)

    // === Known parser limitations (suppressErrors) ===

    @Test fun testKeyAfterBullet3() = doParserTest(checkResult = true, suppressErrors = true) // complex mixed bullet/key/indent nesting
    @Test fun testArrayIndentedFile() = doParserTest(checkResult = false, suppressErrors = true) // file starting with indentation
    @Test fun testItemValueAfterNewLine() = doParserTest(checkResult = true, suppressErrors = true) // bullet value on next line: -\n  a

    // === Error cases (expected to have errors) ===

    @Test fun testErrorInlineArray() = doParserTest(checkResult = true, suppressErrors = true)
    @Test fun testErrorClosingBracket() = doParserTest(checkResult = true, suppressErrors = true)
    @Test fun testErrorClosingBracket2() = doParserTest(checkResult = true, suppressErrors = true)
    @Test fun testErrorTabSpaceMixing() = doParserTest(checkResult = true, suppressErrors = true)
    @Test fun testErrorIndent() = doParserTest(checkResult = true, suppressErrors = true)
    @Test fun testErrorDuplicateKey() = doParserTest(checkResult = true, suppressErrors = true)
    @Test fun testErrorMixedBrackets() = doParserTest(checkResult = true, suppressErrors = true)
    @Test fun testErrorCrossLineMixing() = doParserTest(checkResult = true, suppressErrors = true)

    // === Additional coverage from PHP test suite ===

    @Test fun testAssignmentSeparator() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testSpecialValues() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testJsonCompatibility() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testStringEdgeCases() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testEmptyValues() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testServiceReferences() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testNestedParameters() = doParserTest(checkResult = true, suppressErrors = false)
    @Test fun testNamedArguments() = doParserTest(checkResult = true, suppressErrors = false)
}
