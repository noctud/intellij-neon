package dev.noctud.neon.editor

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.tree.TokenSet
import dev.noctud.neon.lexer.NeonTokenTypes
import dev.noctud.neon.parser.NeonElementTypes

/**
 * Fold sections in Neon
 */
class NeonFoldingBuilder : FoldingBuilder, NeonTokenTypes {
    override fun buildFoldRegions(astNode: ASTNode, document: Document): Array<FoldingDescriptor> {
        val descriptors: MutableList<FoldingDescriptor> = mutableListOf()
        collectDescriptors(astNode, descriptors)
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String {
        val type = node.elementType
        if (type === NeonElementTypes.KEY_VALUE_PAIR) {
            return node.firstChildNode.text
        }

        if (type === NeonElementTypes.SCALAR_VALUE) {
            return node.text[0].toString()
        }

        return "..."
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return false
    }


    companion object {
        private val COMPOUND_VALUE = TokenSet.create(
            NeonElementTypes.COMPOUND_VALUE,
            NeonElementTypes.HASH
        )

        private fun collectDescriptors(node: ASTNode, descriptors: MutableList<FoldingDescriptor>) {
            val type = node.elementType
            val nodeTextRange = node.textRange
            if ((!StringUtil.isEmptyOrSpaces(node.text)) && (nodeTextRange.length >= 2)) {
                if (type === NeonElementTypes.KEY_VALUE_PAIR) {
                    val children = node.getChildren(COMPOUND_VALUE)

                    if ((children.size > 0) && (!StringUtil.isEmpty(children[0].text.trim { it <= ' ' }))) {
                        descriptors.add(FoldingDescriptor(node, nodeTextRange))
                    }
                }
                if (type === NeonElementTypes.SCALAR_VALUE) {
                    descriptors.add(FoldingDescriptor(node, nodeTextRange))
                }
            }
            for (child in node.getChildren(null)) {
                collectDescriptors(child, descriptors)
            }
        }
    }
}
