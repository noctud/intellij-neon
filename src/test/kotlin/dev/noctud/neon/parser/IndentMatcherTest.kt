package dev.noctud.neon.parser

import org.junit.Assert
import org.junit.Test
import dev.noctud.neon.parser.NeonParserUtil.IndentMatcher

class IndentMatcherTest {
    @Test
    fun testIndent() {
        val matcher: IndentMatcher = createMatcher()
        matcher.addIfAbsent("  ")

        Assert.assertSame(1, matcher.indents.size)
        Assert.assertTrue(matcher.match(""))
        Assert.assertTrue(matcher.match("  "))
        Assert.assertFalse(matcher.match("    "))
    }

    companion object {
        private fun createMatcher(): IndentMatcher {
            return IndentMatcher()
        }
    }
}
