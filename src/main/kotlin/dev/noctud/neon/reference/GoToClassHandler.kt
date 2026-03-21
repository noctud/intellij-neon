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
        val scalar = element.parent as NeonScalarImpl
        val phpIndex = PhpIndex.getInstance(element.project)

        // Try FQN first (contains \)
        val fqn = scalar.classFqn
        if (fqn != null) {
            var classes = phpIndex.getAnyByFQN(fqn)
            if (classes.isEmpty() && !fqn.startsWith("\\")) {
                classes = phpIndex.getAnyByFQN("\\$fqn")
            }
            if (classes.isNotEmpty()) return classes.toTypedArray()
        }

        // Try simple class name (PascalCase, no namespace)
        val text = scalar.valueText
        if (text.isNotEmpty() && text[0].isUpperCase() && !text.contains("\\") && !text.contains("/")) {
            val classes = phpIndex.getClassesByName(text) +
                phpIndex.getInterfacesByName(text) +
                phpIndex.getTraitsByName(text)
            if (classes.isNotEmpty()) return classes.toTypedArray()
        }

        return arrayOfNulls(0)
    }

    override fun getActionText(context: DataContext): String? {
        return null
    }
}
