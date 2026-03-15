package dev.noctud.neon.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import dev.noctud.neon.lexer.NeonLexer
import dev.noctud.neon.lexer.NeonTokenTypes
import dev.noctud.neon.psi.impl.elements.*

class NeonParserDefinition : ParserDefinition {
    override fun createLexer(project: Project?): Lexer {
        return NeonLexer()
    }

    override fun createParser(project: Project?): PsiParser {
        return NeonParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return NeonElementTypes.FILE
    }

    override fun getWhitespaceTokens(): TokenSet {
        return NeonTokenTypes.WHITESPACES
    }

    override fun getCommentTokens(): TokenSet {
        return NeonTokenTypes.COMMENTS
    }

    override fun getStringLiteralElements(): TokenSet {
        return NeonTokenTypes.STRING_LITERALS
    }

    override fun createElement(node: ASTNode): PsiElement {
        val type = node.elementType

        if (type === NeonElementTypes.KEY_VALUE_PAIR) return NeonKeyValPairImpl(node)
        else if (type === NeonElementTypes.KEY) return NeonKeyImpl(node)
        else if (type === NeonElementTypes.COMPOUND_VALUE) return NeonArrayImpl(node)
        else if (type === NeonElementTypes.ARRAY) return NeonArrayImpl(node)
        else if (type === NeonElementTypes.SCALAR_VALUE) return NeonScalarImpl(node)
        else if (type === NeonElementTypes.ENTITY) return NeonEntityImpl(node)
        else if (type === NeonElementTypes.CHAINED_ENTITY) return NeonChainedEntityImpl(node)
        else if (type === NeonElementTypes.ARGS) return NeonArrayImpl(node) // FIXME: will it work?
        else return NeonPsiElementImpl(node)
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
}
