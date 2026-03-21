package dev.noctud.neon.reference

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import dev.noctud.neon.ext.findServiceDefinitions
import dev.noctud.neon.lexer._NeonTypes

/**
 * Ctrl+Click navigation for @service references.
 * Navigates to the service definition in the `services:` section.
 */
class GoToServiceHandler : GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(element: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement>? {
        if (element == null) return null
        if (element.node.elementType !== _NeonTypes.T_LITERAL) return null

        val text = element.text
        if (!text.startsWith("@") || text.length < 2) return null
        if (text.contains("\\")) return null

        val serviceName = text.substring(1).substringBefore("::")
        val targets = element.project.findServiceDefinitions(serviceName)

        return if (targets.isEmpty()) null else targets.toTypedArray()
    }

    override fun getActionText(context: DataContext): String? = null
}
