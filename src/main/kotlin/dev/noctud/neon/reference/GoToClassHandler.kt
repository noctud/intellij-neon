package dev.noctud.neon.reference

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import dev.noctud.neon.psi.elements.NeonScalar

class GoToClassHandler : GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(element: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement?> {
        if (element == null || element.parent == null || (element.parent !is NeonScalar)) {
            return arrayOfNulls(0)
        }
        val phpIndex = PhpIndex.getInstance(element.project)
        val classes = phpIndex.getAnyByFQN((element.parent as NeonScalar).valueText)

        return classes.toTypedArray<PsiElement?>()
    }

    override fun getActionText(context: DataContext): String? {
        return null
    }
}
