package dev.noctud.neon.parser

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.tree.IElementType
import dev.noctud.neon.lexer.NeonTypes
import dev.noctud.neon.lexer._NeonTypes

object NeonParserUtil : GeneratedParserUtilBase() {
    /**
     * Per-thread parser state. IntelliJ parses multiple files concurrently,
     * so shared mutable state on the singleton object would cause race conditions.
     */
    private class ParserState {
        val indentStack = mutableListOf<String>()
        var rootIndent: String? = null
    }

    private val state = ThreadLocal.withInitial { ParserState() }

    @JvmStatic
    fun initIndentMatcher(builder: PsiBuilder?, level: Int): Boolean {
        val s = state.get()
        s.indentStack.clear()
        s.rootIndent = null
        return true
    }

    @JvmStatic
    fun isInnerKeyValPair(builder: PsiBuilder, level: Int): Boolean {
        val token = builder.tokenType
        if (token !== _NeonTypes.T_INDENT) {
            return false // Can't start inner array without an indent
        }

        val indentText = getLastIndentText(builder) ?: return false
        val currentIndent = normalizeIndent(indentText)

        val s = state.get()
        val parentIndent = s.indentStack.lastOrNull() ?: s.rootIndent ?: ""
        if (currentIndent.length <= parentIndent.length) {
            return false // Not deeper than parent
        }

        // Verify the next non-indent token is a key element
        val nextToken = getNextTokenAfterIndent(builder)
        if (nextToken == null || !NeonTypes.KEY_ELEMENTS.contains(nextToken)) {
            return false
        }

        s.indentStack.add(currentIndent)
        return true
    }

    @JvmStatic
    fun isSameKeyValPair(builder: PsiBuilder, level: Int): Boolean {
        val token = builder.tokenType
        val s = state.get()

        // If not at an indent token, we're on the same line — check if it's a key element
        if (token !== _NeonTypes.T_INDENT) {
            if (s.rootIndent == null) {
                s.rootIndent = "" // Root level starts without indent (normal case)
            }
            return NeonTypes.KEY_ELEMENTS.contains(token)
        }

        val indentText = getLastIndentText(builder) ?: return false
        val currentIndent = normalizeIndent(indentText)

        // Determine expected indent: stack top for nested blocks, rootIndent for root level
        val expectedIndent = s.indentStack.lastOrNull()

        if (expectedIndent == null) {
            // At root level
            if (s.rootIndent == null) {
                // First indented root item — establish root indent
                val nextToken = getNextTokenAfterIndent(builder)
                if (nextToken != null && NeonTypes.KEY_ELEMENTS.contains(nextToken)) {
                    s.rootIndent = currentIndent
                    return true
                }
                return false
            }
            // Subsequent root items
            if (currentIndent == s.rootIndent) {
                val nextToken = getNextTokenAfterIndent(builder)
                return nextToken != null && NeonTypes.KEY_ELEMENTS.contains(nextToken)
            }
            return false
        }

        if (currentIndent == expectedIndent) {
            // Verify the next non-indent token is a key element
            val nextToken = getNextTokenAfterIndent(builder)
            return nextToken != null && NeonTypes.KEY_ELEMENTS.contains(nextToken)
        }

        if (currentIndent.length < expectedIndent.length) {
            // We've left this block — pop the stack entry for this level
            // so parent levels can match
            s.indentStack.removeLast()
            return false
        }

        // currentIndent > expectedIndent — this is a deeper indent, not same level
        return false
    }

    private fun normalizeIndent(indent: String): String {
        return indent.replace("\n", "")
    }

    /**
     * Get the text of the last consecutive T_INDENT token at the current position.
     * Uses mark/rollback to not consume tokens.
     */
    private fun getLastIndentText(builder: PsiBuilder): String? {
        val marker = builder.mark()
        var lastText = builder.tokenText
        while (builder.tokenType === _NeonTypes.T_INDENT) {
            lastText = builder.tokenText
            builder.advanceLexer()
            if (builder.tokenType !== _NeonTypes.T_INDENT) break
        }
        marker.rollbackTo()
        return lastText
    }

    /**
     * Get the token type of the first non-indent token after the current position.
     */
    private fun getNextTokenAfterIndent(builder: PsiBuilder): IElementType? {
        val marker = builder.mark()
        while (builder.tokenType === _NeonTypes.T_INDENT) {
            builder.advanceLexer()
        }
        val result = builder.tokenType
        marker.rollbackTo()
        return result
    }

    // Keep IndentMatcher for the test that uses it directly
    class IndentMatcher {
        @JvmField
        val indents: MutableList<String> = ArrayList()
        private var length = 0

        fun addIfAbsent(indent: String): Boolean {
            val level = getLevel(indent)
            if (level != ERROR_ADDITIONS) {
                return level != ERROR_INVALID
            }

            val addition = indent.substring(length)
            if (addition.isNotEmpty()) {
                indents.add(addition)
                length += addition.length
            }
            return true
        }

        fun match(indent: String): Boolean {
            return getLevel(indent) >= 0
        }

        fun getLevel(indent: String): Int {
            var level = 0
            var offset = 0
            for (current in indents) {
                val currentLength = current.length
                if (offset + currentLength > indent.length) {
                    if (indent.substring(offset).isNotEmpty()) {
                        return ERROR_INVALID
                    }
                    break
                }

                if (indent.substring(offset, offset + currentLength) == current) {
                    level++
                    offset += currentLength
                } else {
                    return ERROR_INVALID
                }
            }

            if (indent.substring(offset).isNotEmpty()) {
                return ERROR_ADDITIONS
            }
            return level
        }

        companion object {
            private val ERROR_INVALID = -1
            private val ERROR_ADDITIONS = -2
        }
    }
}
