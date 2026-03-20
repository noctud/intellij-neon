package dev.noctud.neon.editor

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.tree.TokenSet
import dev.noctud.neon.lexer._NeonTypes

/**
 * Fold sections in Neon
 */
class NeonFoldingBuilder : FoldingBuilder {
    override fun buildFoldRegions(astNode: ASTNode, document: Document): Array<FoldingDescriptor> {
        val descriptors: MutableList<FoldingDescriptor> = mutableListOf()
        collectDescriptors(astNode, descriptors)
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String {
        val type = node.elementType
        if (type === _NeonTypes.KEY_VAL_PAIR) {
            return node.firstChildNode.text
        }

        if (type === _NeonTypes.SCALAR) {
            return node.text[0].toString()
        }

        return "..."
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return false
    }


    companion object {
        private val FOLDABLE_VALUES = TokenSet.create(
            _NeonTypes.VALUE,
            _NeonTypes.INNER_ARRAY,
            _NeonTypes.INLINE_ARRAY
        )

        private fun collectDescriptors(node: ASTNode, descriptors: MutableList<FoldingDescriptor>) {
            val type = node.elementType
            val nodeTextRange = node.textRange
            if ((!StringUtil.isEmptyOrSpaces(node.text)) && (nodeTextRange.length >= 2)) {
                if (type === _NeonTypes.KEY_VAL_PAIR) {
                    val children = node.getChildren(FOLDABLE_VALUES)

                    if ((children.isNotEmpty()) && (!StringUtil.isEmpty(children[0].text.trim { it <= ' ' }))) {
                        descriptors.add(FoldingDescriptor(node, nodeTextRange))
                    }
                }
                if (type === _NeonTypes.SCALAR) {
                    descriptors.add(FoldingDescriptor(node, nodeTextRange))
                }
            }
            for (child in node.getChildren(null)) {
                collectDescriptors(child, descriptors)
            }
        }
    }
}
