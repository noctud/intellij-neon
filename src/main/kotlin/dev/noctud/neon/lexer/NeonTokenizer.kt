package dev.noctud.neon.lexer

import com.intellij.openapi.util.Pair
import com.intellij.psi.tree.IElementType
import java.util.regex.Matcher
import java.util.regex.Pattern

object NeonTokenizer {
    private val patterns: Array<String> = arrayOf(
        "\'[^\'\n]*\'|\"(?:\\\\.|[^\"\\\\\n])*\" ",  // string
        "-(?=\\s|$)|:(?=[\\s,\\]})]|$)|[,=\\[\\]{}\\(\\)]",  // symbol
        "#.*",  // comment
        "\n[\t ]*",  // new line + indent
        "[^#\"\',=\\[\\]{}\\(\\)\\x00-\\x20!`](?:[^,:=\\]}\\)\\(\\x00-\\x20]+|:(?![\\s,\\]})]|$)|[ \t]+[^#,:=\\]}\\)\\(\\x00-\\x20])*",  // literal / boolean / integer / float
        "[\t ]+" // whitespace
    )

    @JvmStatic
    fun parse(string: String): ArrayList<Pair<IElementType?, String?>?> {
        val tokens = tokenize(string)



        return tokens
    }

    private fun tokenize(string: String): ArrayList<Pair<IElementType?, String?>?> {
        val tokens = ArrayList<Pair<IElementType?, String?>?>()

        val p = createPattern()
        val m = p.matcher(string)
        while (m.find()) {
            val p2 = getToken(m)
            if (p2 != null) tokens.add(p2)
        }

        return tokens
    }

    fun getToken(m: Matcher): Pair<IElementType?, String?>? {
        val p: Pair<IElementType?, String?>?

        if (m.group(1) != null) p = Pair<IElementType?, String?>(NeonTokenTypes.NEON_STRING, m.group())
        else if (m.group(2) != null) p = Pair<IElementType?, String?>(NeonTokenTypes.NEON_SYMBOL, m.group())
        else if (m.group(3) != null) p = Pair<IElementType?, String?>(NeonTokenTypes.NEON_COMMENT, m.group())
        else if (m.group(4) != null) p = Pair<IElementType?, String?>(NeonTokenTypes.NEON_INDENT, m.group())
        else if (m.group(5) != null) p = Pair<IElementType?, String?>(NeonTokenTypes.NEON_LITERAL, m.group())
        else if (m.group(6) != null) p = Pair<IElementType?, String?>(NeonTokenTypes.NEON_WHITESPACE, m.group())
        else p = null

        return p
    }

    fun createPattern(): Pattern {
        return Pattern.compile("(?:(" + implode(")|(", *patterns) + "))")
    }

    fun implode(separator: String?, vararg data: String?): String {
        val sb = StringBuilder()
        for (i in 0 until data.size - 1) {
            //data.length - 1 => to not add separator at the end
            if (!data[i]!!.matches(" *".toRegex())) { //empty string are ""; " "; "  "; and so on
                sb.append(data[i])
                sb.append(separator)
            }
        }
        sb.append(data[data.size - 1])
        return sb.toString()
    }
}
