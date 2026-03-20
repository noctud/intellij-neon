package dev.noctud.neon.editor

import com.intellij.application.options.CodeStyle
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilCore
import dev.noctud.neon.NeonLanguage
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonArray
import dev.noctud.neon.psi.elements.NeonFile
import dev.noctud.neon.psi.elements.NeonKeyValPair

class NeonEnterHandler : EnterHandlerDelegate {
    override fun preprocessEnter(
        file: PsiFile,
        editor: Editor,
        caretOffset: Ref<Int?>,
        caretAdvance: Ref<Int?>,
        dataContext: DataContext,
        originalHandler: EditorActionHandler?
    ): EnterHandlerDelegate.Result {
        return EnterHandlerDelegate.Result.Continue
    }

    override fun postProcessEnter(
        file: PsiFile,
        editor: Editor,
        dataContext: DataContext
    ): EnterHandlerDelegate.Result? {
        if (file !is NeonFile) {
            return null
        }
        val offset = editor.caretModel.offset
        val document = editor.document
        val el = PsiUtilCore.getElementAtOffset(file, offset - 1)
        val prev1 = PsiUtilCore.getElementAtOffset(file, offset - el.text.length - 1)
        val prev2 = PsiUtilCore.getElementAtOffset(file, offset - el.text.length - 2)
        if (prev1.text == "\"" && prev2.text == "\"\"") {
            document.insertString(offset, "\n" + el.text.substring(1) + "\"\"\"")
            return null
        } else if (prev1.text == "'" && prev2.text == "''") {
            document.insertString(offset, "\n" + el.text.substring(1) + "'''")
            return null
        }
        if (!shouldProcess(el)) {
            return null
        }
        val indent = getIndentString(file, isKeyAfterBullet(el.parent))
        document.insertString(offset, indent)
        editor.caretModel.moveToOffset(offset + indent.length)
        EditorModificationUtil.scrollToCaret(editor)
        editor.selectionModel.removeSelection()
        return null
    }

    private fun shouldProcess(el: PsiElement): Boolean {
        val prev = el.prevSibling
        if (prev != null && prev.node.elementType === _NeonTypes.T_ITEM_DELIMITER) { //inline array
            return false
        }
        if (prev != null && prev.node.elementType === _NeonTypes.T_INDENT) { //empty line
            return false
        }
        val parent = el.parent ?: return false
        return parent is NeonKeyValPair || isKeyAfterBullet(parent)
    }

    private fun getIndentString(file: PsiFile, keyAfterBullet: Boolean): String {
        if (keyAfterBullet) {
            return "  "
        }

        val settings = CodeStyle.getLanguageSettings(file, NeonLanguage.INSTANCE)
        val indentOpt = settings.indentOptions
        return if (indentOpt == null || indentOpt.USE_TAB_CHARACTER) "\t" else StringUtil.repeat(
            " ",
            indentOpt.INDENT_SIZE
        )
    }

    private fun isKeyAfterBullet(el: PsiElement?): Boolean {
        var element = el
        element = if (element is NeonKeyValPair) element.parent else element

        return element is NeonArray
                && element.children.size == 1 && element.parent is NeonKeyValPair
                && element.prevSibling?.text == " "
                && element.parent.firstChild?.text == "-"
    }
}
