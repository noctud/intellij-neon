package dev.noctud.neon.lexer

import com.intellij.openapi.util.Pair
import com.intellij.psi.tree.IElementType
import com.intellij.testFramework.UsefulTestCase
import org.junit.Test
import dev.noctud.neon.lexer.NeonTokenizer.parse

class NeonTokenizerTest : UsefulTestCase() {
    @Test
    fun testParse() {
        val expected = ArrayList<Pair<IElementType?, String?>?>()
        expected.add(Pair<IElementType?, String?>(NeonTokenTypes.NEON_LITERAL, "key"))
        expected.add(Pair<IElementType?, String?>(NeonTokenTypes.NEON_SYMBOL, ":"))
        expected.add(Pair<IElementType?, String?>(NeonTokenTypes.NEON_WHITESPACE, " "))
        expected.add(Pair<IElementType?, String?>(NeonTokenTypes.NEON_STRING, "'value'"))

        assertOrderedEquals<Pair<IElementType?, String?>?>(parse("key: 'value'"), expected)
    }
}
