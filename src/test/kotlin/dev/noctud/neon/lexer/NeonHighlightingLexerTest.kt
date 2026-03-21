package dev.noctud.neon.lexer

import com.intellij.lexer.Lexer
import com.intellij.psi.TokenType
import com.intellij.testFramework.UsefulTestCase
import dev.noctud.neon.annotator.NeonAnnotator
import junit.framework.TestCase
import org.junit.Test

class NeonHighlightingLexerTest : UsefulTestCase() {

    private fun createLexer(text: String): Lexer {
        val l = NeonHighlightingLexer(NeonLexer())
        l.start(text)
        return l
    }

    private fun assertToken(l: Lexer, type: com.intellij.psi.tree.IElementType?, text: String) {
        assertEquals(type, l.tokenType)
        TestCase.assertEquals(text, l.tokenText)
        l.advance()
    }

    private fun skipTo(l: Lexer, text: String) {
        while (l.tokenType != null && l.tokenText != text) l.advance()
    }

    // === Key detection ===

    @Test
    fun testKeys() {
        val l = createLexer("key: val")
        assertToken(l, NeonTokenTypes.NEON_KEY, "key")
        assertToken(l, _NeonTypes.T_COLON, ":")
        assertToken(l, TokenType.WHITE_SPACE, " ")
        assertToken(l, NeonTokenTypes.NEON_STRING, "val")
        assertToken(l, null, "")
    }

    @Test
    fun testKeyWithEquals() {
        val l = createLexer("key= val")
        assertToken(l, NeonTokenTypes.NEON_KEY, "key")
    }

    // === Keywords (true/false/null/yes/no/on/off) ===

    @Test
    fun testKeywords() {
        val l = createLexer("[true,off,TruE,\"true\"]")
        assertToken(l, _NeonTypes.T_LBRACE_SQUARE, "[")
        assertToken(l, NeonTokenTypes.NEON_KEYWORD, "true")
        assertToken(l, _NeonTypes.T_ITEM_DELIMITER, ",")
        assertToken(l, NeonTokenTypes.NEON_KEYWORD, "off")
        assertToken(l, _NeonTypes.T_ITEM_DELIMITER, ",")
        assertToken(l, NeonTokenTypes.NEON_STRING, "TruE") // not a keyword — wrong case
        assertToken(l, _NeonTypes.T_ITEM_DELIMITER, ",")
        assertToken(l, NeonTokenTypes.NEON_STRING, "\"true\"") // quoted string, not keyword
        assertToken(l, _NeonTypes.T_RBRACE_SQUARE, "]")
        assertToken(l, null, "")
    }

    @Test
    fun testAllKeywordVariants() {
        for (kw in listOf("true", "True", "TRUE", "false", "False", "FALSE",
            "yes", "Yes", "YES", "no", "No", "NO",
            "on", "On", "ON", "off", "Off", "OFF",
            "null", "Null", "NULL")) {
            val l = createLexer("[$kw]")
            l.advance() // skip [
            assertEquals("$kw should be NEON_KEYWORD", NeonTokenTypes.NEON_KEYWORD, l.tokenType)
        }
    }

    // === Numbers ===

    @Test
    fun testNumbers() {
        for ((input, desc) in listOf(
            "42" to "integer",
            "-1" to "negative",
            "3.14" to "float",
            "0" to "zero",
            "-0.5" to "negative float",
            "1.5e3" to "scientific",
            "1.5E-3" to "scientific negative",
            "0xff" to "hex",
            "0xFF" to "hex upper",
            "0o777" to "octal",
            "0b1010" to "binary",
        )) {
            val l = createLexer("[$input]")
            l.advance() // skip [
            assertEquals("$input ($desc) should be NEON_NUMBER", NeonTokenTypes.NEON_NUMBER, l.tokenType)
        }
    }

    @Test
    fun testNotNumbers() {
        for ((input, desc) in listOf(
            "42px" to "number with suffix",
            "-1e" to "incomplete scientific",
            "1e+-1" to "invalid scientific",
            "--1" to "double minus",
        )) {
            val l = createLexer("[$input]")
            l.advance() // skip [
            assertFalse("$input ($desc) should NOT be NEON_NUMBER",
                l.tokenType == NeonTokenTypes.NEON_NUMBER)
        }
    }

    // === Datetime ===

    @Test
    fun testDatetime() {
        for ((input, desc) in listOf(
            "2025-10-20" to "date only",
            "2025-1-5" to "short date",
            "2025-10-20T11:44:55" to "ISO datetime",
        )) {
            val l = createLexer("[$input]")
            l.advance() // skip [
            assertEquals("$input ($desc) should be NEON_DATETIME", NeonTokenTypes.NEON_DATETIME, l.tokenType)
        }
    }

    // === Strings ===

    @Test
    fun testUnquotedStrings() {
        val l = createLexer("[hello, mariadb-11.8.2, Europe/Prague]")
        l.advance() // skip [
        assertToken(l, NeonTokenTypes.NEON_STRING, "hello")
        assertToken(l, _NeonTypes.T_ITEM_DELIMITER, ",")
        l.advance() // skip space
        assertToken(l, NeonTokenTypes.NEON_STRING, "mariadb-11.8.2")
    }

    @Test
    fun testQuotedStrings() {
        val l = createLexer("a: 'quoted'")
        skipTo(l, "'quoted'")
        assertToken(l, NeonTokenTypes.NEON_STRING, "'quoted'")
    }

    // === PHP class names ===

    @Test
    fun testClassNames() {
        val l = createLexer("[App\\Model\\Foo]")
        l.advance() // skip [
        assertToken(l, NeonTokenTypes.NEON_CLASSNAME, "App\\Model\\Foo")
    }

    // === Service references ===

    @Test
    fun testServiceRef() {
        val l = createLexer("[@routerFactory]")
        l.advance() // skip [
        assertToken(l, NeonTokenTypes.NEON_SERVICE_REF, "@routerFactory")
    }

    @Test
    fun testServiceRefWithNamespace() {
        val l = createLexer("[@App\\Model\\Handler]")
        l.advance() // skip [
        assertToken(l, NeonTokenTypes.NEON_SERVICE_REF, "@App\\Model\\Handler")
    }

    @Test
    fun testServiceRefWithMethod() {
        val l = createLexer("[@factory::create]")
        l.advance() // skip [
        assertToken(l, NeonTokenTypes.NEON_SERVICE_REF, "@factory::create")
    }

    // === File paths ===

    @Test
    fun testFilePath() {
        val l = createLexer("[app/Model/Foo.php]")
        l.advance() // skip [
        assertToken(l, NeonTokenTypes.NEON_FILEPATH, "app/Model/Foo.php")
    }

    @Test
    fun testFilePathNoSlash() {
        // No slash but ends with .neon — still a file path
        val l = createLexer("[services.neon]")
        l.advance() // skip [
        assertToken(l, NeonTokenTypes.NEON_FILEPATH, "services.neon")
    }

    // === isNumeric unit tests ===

    @Test
    fun testIsNumeric() {
        assertTrue(NeonHighlightingLexer.isNumeric("0"))
        assertTrue(NeonHighlightingLexer.isNumeric("42"))
        assertTrue(NeonHighlightingLexer.isNumeric("-1"))
        assertTrue(NeonHighlightingLexer.isNumeric("3.14"))
        assertTrue(NeonHighlightingLexer.isNumeric("1.5e3"))
        assertTrue(NeonHighlightingLexer.isNumeric("1.5E-3"))
        assertTrue(NeonHighlightingLexer.isNumeric("0xff"))
        assertTrue(NeonHighlightingLexer.isNumeric("0o777"))
        assertTrue(NeonHighlightingLexer.isNumeric("0b1010"))

        assertFalse(NeonHighlightingLexer.isNumeric("hello"))
        assertFalse(NeonHighlightingLexer.isNumeric("42px"))
        assertFalse(NeonHighlightingLexer.isNumeric(""))
        assertFalse(NeonHighlightingLexer.isNumeric("-1e"))
        assertFalse(NeonHighlightingLexer.isNumeric("1e+-1"))
    }

    // === Named arguments in entity params — lexer still shows as KEY, annotator recolors ===

    @Test
    fun testNamedArgInEntityIsKey() {
        // In "Foo(key: val)", "key" is followed by ":" so the highlighting lexer marks it as KEY
        val l = createLexer("Foo(key: val)")
        assertToken(l, NeonTokenTypes.NEON_STRING, "Foo") // class name without \ is string
        assertToken(l, _NeonTypes.T_LPAREN, "(")
        assertToken(l, NeonTokenTypes.NEON_KEY, "key") // highlighted as key by lexer
    }

    // === Simple class name detection (from NeonAnnotator) ===

    @Test
    fun testLooksLikeSimpleClassName() {
        // Valid PHP class names
        assertTrue(NeonAnnotator.looksLikeSimpleClassName("Throwable"))
        assertTrue(NeonAnnotator.looksLikeSimpleClassName("Exception"))
        assertTrue(NeonAnnotator.looksLikeSimpleClassName("DateTime"))
        assertTrue(NeonAnnotator.looksLikeSimpleClassName("SplFixedArray"))

        // Not class names
        assertFalse(NeonAnnotator.looksLikeSimpleClassName("throwable")) // lowercase
        assertFalse(NeonAnnotator.looksLikeSimpleClassName("T")) // single char
        assertFalse(NeonAnnotator.looksLikeSimpleClassName("App\\Model")) // FQN
        assertFalse(NeonAnnotator.looksLikeSimpleClassName("@Service")) // service ref
        assertFalse(NeonAnnotator.looksLikeSimpleClassName("%appDir%")) // variable
        assertFalse(NeonAnnotator.looksLikeSimpleClassName("app/test.php")) // path
        assertFalse(NeonAnnotator.looksLikeSimpleClassName("date.timezone")) // dotted
        assertFalse(NeonAnnotator.looksLikeSimpleClassName("*Data")) // wildcard
        assertFalse(NeonAnnotator.looksLikeSimpleClassName("")) // empty
    }

    // === isFilePath unit tests ===

    @Test
    fun testIsFilePath() {
        assertTrue(NeonHighlightingLexer.isFilePath("app/Model/Foo.php"))
        assertTrue(NeonHighlightingLexer.isFilePath("src/test.neon"))
        assertTrue(NeonHighlightingLexer.isFilePath("tests/Unit/FooTest.php"))
        assertTrue(NeonHighlightingLexer.isFilePath("services.neon"))
        assertTrue(NeonHighlightingLexer.isFilePath("Foo.php"))

        assertFalse(NeonHighlightingLexer.isFilePath("app/Model")) // no extension
        assertFalse(NeonHighlightingLexer.isFilePath("hello")) // plain string
        assertFalse(NeonHighlightingLexer.isFilePath("Europe/Prague")) // not .php or .neon
        assertFalse(NeonHighlightingLexer.isFilePath("date.timezone")) // dot but wrong extension
    }

    // === isDateTime unit tests ===

    @Test
    fun testIsDateTime() {
        assertTrue(NeonHighlightingLexer.isDateTime("2025-10-20"))
        assertTrue(NeonHighlightingLexer.isDateTime("2025-1-5"))
        assertTrue(NeonHighlightingLexer.isDateTime("2025-10-20T11:44:55"))
        assertTrue(NeonHighlightingLexer.isDateTime("2025-10-20 11:44:55"))
        assertTrue(NeonHighlightingLexer.isDateTime("2025-10-20 11:44:55.1234"))
        assertTrue(NeonHighlightingLexer.isDateTime("2025-10-20 11:44:55+0200"))
        assertTrue(NeonHighlightingLexer.isDateTime("2025-10-20 11:44:55Z"))

        assertFalse(NeonHighlightingLexer.isDateTime("hello"))
        assertFalse(NeonHighlightingLexer.isDateTime("2025"))
        assertFalse(NeonHighlightingLexer.isDateTime("2025-10"))
        assertFalse(NeonHighlightingLexer.isDateTime("10-20-2025"))
    }
}
