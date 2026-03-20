package dev.noctud.neon.parser

import org.junit.Assert
import org.junit.Test
import dev.noctud.neon.parser.NeonParserUtil.IndentMatcher

class IndentMatcherTest {
    @Test
    fun testBasicIndent() {
        val matcher = IndentMatcher()
        matcher.addIfAbsent("  ")

        Assert.assertSame(1, matcher.indents.size)
        Assert.assertTrue(matcher.match(""))
        Assert.assertTrue(matcher.match("  "))
        Assert.assertFalse(matcher.match("    "))
    }

    @Test
    fun testMultipleLevels() {
        val matcher = IndentMatcher()
        matcher.addIfAbsent("  ")
        matcher.addIfAbsent("    ")

        Assert.assertSame(2, matcher.indents.size)
        Assert.assertTrue(matcher.match(""))
        Assert.assertTrue(matcher.match("  "))
        Assert.assertTrue(matcher.match("    "))
        Assert.assertFalse(matcher.match("      "))
    }

    @Test
    fun testTabIndent() {
        val matcher = IndentMatcher()
        matcher.addIfAbsent("\t")
        matcher.addIfAbsent("\t\t")

        Assert.assertTrue(matcher.match(""))
        Assert.assertTrue(matcher.match("\t"))
        Assert.assertTrue(matcher.match("\t\t"))
        Assert.assertFalse(matcher.match("\t\t\t"))
    }

    @Test
    fun testInvalidIndent() {
        val matcher = IndentMatcher()
        matcher.addIfAbsent("  ")

        // Three spaces doesn't align with 2-space indent
        Assert.assertFalse(matcher.match("   "))
    }

    @Test
    fun testGetLevel() {
        val matcher = IndentMatcher()
        matcher.addIfAbsent("  ")
        matcher.addIfAbsent("    ")

        Assert.assertEquals(0, matcher.getLevel(""))
        Assert.assertEquals(1, matcher.getLevel("  "))
        Assert.assertEquals(2, matcher.getLevel("    "))
    }

    @Test
    fun testEmptyIndent() {
        val matcher = IndentMatcher()
        Assert.assertTrue(matcher.match(""))
        Assert.assertFalse(matcher.match("  "))
    }

    @Test
    fun testAddIfAbsentIdempotent() {
        val matcher = IndentMatcher()
        Assert.assertTrue(matcher.addIfAbsent("  "))
        Assert.assertTrue(matcher.addIfAbsent("  ")) // same indent again — should still succeed
        Assert.assertSame(1, matcher.indents.size)
    }
}
