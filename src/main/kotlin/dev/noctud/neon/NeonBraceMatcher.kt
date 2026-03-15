package dev.noctud.neon

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import dev.noctud.neon.lexer.NeonTokenTypes

class NeonBraceMatcher : PairedBraceMatcher, NeonTokenTypes {
    override fun getPairs(): Array<BracePair?> {
        return PAIRS
    }

    override fun isPairedBracesAllowedBeforeType(iElementType: IElementType, iElementType1: IElementType?): Boolean {
        return true
    }

    override fun getCodeConstructStart(psiFile: PsiFile?, openingBraceOffset: Int): Int {
        return openingBraceOffset
    }

    companion object {
        private val PAIRS = arrayOf<BracePair?>(
            BracePair(NeonTokenTypes.NEON_LPAREN, NeonTokenTypes.NEON_RPAREN, true),  // ()
            BracePair(NeonTokenTypes.NEON_LBRACE_CURLY, NeonTokenTypes.NEON_RBRACE_CURLY, true),  // {}
            BracePair(NeonTokenTypes.NEON_LBRACE_SQUARE, NeonTokenTypes.NEON_RBRACE_SQUARE, true) // []
        )
    }
}
