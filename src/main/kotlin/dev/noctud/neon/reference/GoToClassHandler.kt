package dev.noctud.neon.reference

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import dev.noctud.neon.psi.impl.elements.NeonScalarImpl

class GoToClassHandler : GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(element: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement?> {
        if (element == null || element.parent == null || element.parent !is NeonScalarImpl) {
            return arrayOfNulls(0)
        }
        val fqn = (element.parent as NeonScalarImpl).classFqn ?: return arrayOfNulls(0)

        val phpIndex = PhpIndex.getInstance(element.project)
        var classes = phpIndex.getAnyByFQN(fqn)
        if (classes.isEmpty() && !fqn.startsWith("\\")) {
            classes = phpIndex.getAnyByFQN("\\$fqn")
        }

        return classes.toTypedArray<PsiElement?>()
    }

    override fun getActionText(context: DataContext): String? {
        return null
    }
}
