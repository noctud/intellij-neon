package dev.noctud.neon

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import dev.noctud.neon.lexer._NeonTypes

class NeonBraceMatcher : PairedBraceMatcher {
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
            BracePair(_NeonTypes.T_LPAREN, _NeonTypes.T_RPAREN, true),
            BracePair(_NeonTypes.T_LBRACE_CURLY, _NeonTypes.T_RBRACE_CURLY, true),
            BracePair(_NeonTypes.T_LBRACE_SQUARE, _NeonTypes.T_RBRACE_SQUARE, true)
        )
    }
}
