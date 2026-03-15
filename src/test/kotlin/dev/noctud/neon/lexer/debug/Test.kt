package dev.noctud.neon.lexer.debug

import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.psi.tree.IElementType
import dev.noctud.neon.lexer._NeonLexer
import java.io.File
import java.io.Reader

object Test {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        // Load file from command line
        var code = FileUtil.loadFile(File(args[0]), CharsetToolkit.UTF8).trim { it <= ' ' }
        code = StringUtil.convertLineSeparators(code)

        val lexer = _NeonLexer(null as Reader?)
        var el: IElementType?

        lexer.reset(code, 0, code.length, _NeonLexer.YYINITIAL)

        while ((lexer.advance().also { el = it }) != null) {
            System.out.printf(
                "%s: %d %d '%s'\n",
                el.toString(),
                lexer.tokenStart,
                lexer.tokenEnd,
                code.substring(lexer.tokenStart, lexer.tokenEnd)
            )
        }
    }
}
