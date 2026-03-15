package dev.noctud.neon.lexer

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet

object NeonTypes : _NeonTypes {
    var WHITESPACES: TokenSet = TokenSet.create(_NeonTypes.T_WHITESPACE, TokenType.WHITE_SPACE)
    var COMMENTS: TokenSet = TokenSet.create(_NeonTypes.T_COMMENT)
    var STRING_LITERALS: TokenSet = TokenSet.create(_NeonTypes.T_LITERAL, _NeonTypes.T_STRING)
    @JvmField
    var KEY_ELEMENTS: TokenSet =
        TokenSet.create(_NeonTypes.T_STRING, _NeonTypes.T_LITERAL, _NeonTypes.T_NUMBER, _NeonTypes.T_ARRAY_BULLET)

    fun createElement(node: ASTNode): PsiElement {
        return _NeonTypes.Factory.createElement(node)
    }
}
