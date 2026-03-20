package dev.noctud.neon.lexer

import com.intellij.lexer.MergingLexerAdapter
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import java.io.Reader

class NeonLexer : MergingLexerAdapter(FlexAdapter(_NeonLexer(null as Reader?)), TokenSet.create(
    _NeonTypes.T_COMMENT,
    TokenType.WHITE_SPACE
))
