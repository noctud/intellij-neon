package dev.noctud.neon.parser

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.tree.IElementType
import dev.noctud.neon.lexer.NeonTypes
import dev.noctud.neon.lexer._NeonTypes
import kotlin.Boolean
import kotlin.Int
import kotlin.checkNotNull
import kotlin.text.substring

object NeonParserUtil : GeneratedParserUtilBase() {
    private var indentMatcher: IndentMatcher? = null

    @JvmStatic
    fun initIndentMatcher(builder: PsiBuilder?, level: Int): Boolean {
        indentMatcher = IndentMatcher()
        return true
    }

    private fun getIndentMatcher(): IndentMatcher {
        checkNotNull(indentMatcher)
        return indentMatcher!!
    }

    fun isComment(builder: PsiBuilder, level: Int): Boolean {
        if (builder.tokenType !== _NeonTypes.T_INDENT) {
            return builder.tokenType === _NeonTypes.T_COMMENT
        }
        return getNextTokenAfterIndent(builder) === _NeonTypes.T_COMMENT
    }

    @JvmStatic
    fun isInnerKeyValPair(builder: PsiBuilder, level: Int): Boolean {
        return isKeyValPair(builder, level, Check.INNER)
    }

    @JvmStatic
    fun isSameKeyValPair(builder: PsiBuilder, level: Int): Boolean {
        return isKeyValPair(builder, level, Check.SAME_LEVEL)
    }

    private fun isKeyValPair(builder: PsiBuilder, level: Int, check: Check?): Boolean {
        val token = builder.tokenType
        if (token !== _NeonTypes.T_INDENT) {
            return NeonTypes.KEY_ELEMENTS.contains(token)
        }

        val indent = getLastTokenWithType(builder, _NeonTypes.T_INDENT) ?: return true

        val current = normalizeIndent(indent)
        val success = getIndentMatcher().addIfAbsent(current.javaClass.toString())
        if (!success) {
            builder.error("Bad indent")
            return false
        }

        var indentLevel = getIndentMatcher().getLevel(current)
        if (indentLevel < 0) {
            builder.error("Bad indent")
            return false
        }

        indentLevel = (indentLevel + 1) * 4
        if (check == Check.INNER) {
            return indentLevel > (level - 4)
        } else if (check == Check.SAME_LEVEL) {
            return indentLevel == (if (level % 4 == 0) level else (level - 2))
        }
        return indentLevel < level
    }

    private fun normalizeIndent(indent: String): String {
        return indent.replace("\n", "")
    }

    private fun getLastTokenWithType(builder: PsiBuilder, type: IElementType?): String? {
        val marker = builder.mark()
        val indent = getLastTokenWithType(builder, type, builder.tokenText)
        marker.rollbackTo()
        return indent
    }

    private fun getNextTokenAfterIndent(builder: PsiBuilder): IElementType? {
        val marker = builder.mark()
        val nextToken = getNextTokenAfterIndent(builder, _NeonTypes.T_INDENT)
        marker.rollbackTo()
        return nextToken
    }

    private fun getNextTokenAfterIndent(builder: PsiBuilder, type: IElementType?): IElementType? {
        if (builder.tokenType === type) {
            builder.advanceLexer()
            return getNextTokenAfterIndent(builder, type)
        }
        return builder.tokenType
    }

    private fun getLastTokenWithType(builder: PsiBuilder, type: IElementType?, prevIndent: String?): String? {
        if (builder.tokenType === type) {
            val current = builder.tokenText
            builder.advanceLexer()
            return getLastTokenWithType(builder, type, current)
        }
        return prevIndent
    }

    private enum class Check {
        SAME_LEVEL,
        INNER,
    }

    class IndentMatcher {
        @JvmField
        val indents: MutableList<String> = ArrayList<String>()
        private var length = 0

        override fun toString(): String {
            return indents.joinToString("")
        }

        fun addIfAbsent(indent: kotlin.String): Boolean {
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

        fun match(indent: kotlin.String): Boolean {
            return getLevel(indent) < 0
        }

        fun getLevel(indent: kotlin.String): Int {
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
