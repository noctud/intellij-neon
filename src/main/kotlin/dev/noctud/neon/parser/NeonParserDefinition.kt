package dev.noctud.neon.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import dev.noctud.neon.NeonLanguage
import dev.noctud.neon.lexer.NeonLexer
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.impl.elements.*

class NeonParserDefinition : ParserDefinition {
    override fun createLexer(project: Project?): Lexer {
        return NeonLexer()
    }

    override fun createParser(project: Project?): PsiParser {
        return NeonParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun getWhitespaceTokens(): TokenSet {
        return TokenSet.create(TokenType.WHITE_SPACE)
    }

    override fun getCommentTokens(): TokenSet {
        return TokenSet.create(_NeonTypes.T_COMMENT)
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.create(_NeonTypes.T_LITERAL, _NeonTypes.T_STRING)
    }

    override fun createElement(node: ASTNode): PsiElement {
        val type = node.elementType

        // Use hand-written impls for types that have custom business logic
        if (type === _NeonTypes.ARRAY) return NeonArrayImpl(node)
        if (type === _NeonTypes.INNER_ARRAY) return NeonArrayImpl(node)
        if (type === _NeonTypes.INLINE_ARRAY) return NeonArrayImpl(node)
        if (type === _NeonTypes.KEY_VAL_PAIR) return NeonKeyValPairImpl(node)
        if (type === _NeonTypes.NAMED_KEY_VAL_PAIR) return NeonKeyValPairImpl(node)
        if (type === _NeonTypes.BULLET_KEY_VAL_PAIR) return NeonKeyValPairImpl(node)
        if (type === _NeonTypes.KEY) return NeonKeyImpl(node)
        if (type === _NeonTypes.SCALAR) return NeonScalarImpl(node)

        // Use generated factory for all other types
        return _NeonTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return NeonFileImpl(viewProvider)
    }

    override fun spaceExistenceTypeBetweenTokens(
        astNode: ASTNode?,
        astNode1: ASTNode?
    ): ParserDefinition.SpaceRequirements {
        return ParserDefinition.SpaceRequirements.MAY
    }

    companion object {
        @JvmField val FILE: IFileElementType = IFileElementType(NeonLanguage.INSTANCE)
    }
}
