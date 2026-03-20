package dev.noctud.neon.reference

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import dev.noctud.neon.ext.isPhpStan
import dev.noctud.neon.file.NeonFileType
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonArray
import dev.noctud.neon.psi.elements.NeonFile

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
        // @Class\Namespace handled by GoToClassHandler
        if (text.contains("\\")) return null

        val serviceName = text.substring(1).substringBefore("::")

        val targets = mutableListOf<PsiElement>()
        val project = element.project
        val scope = GlobalSearchScope.projectScope(project)
        val neonFiles = FileTypeIndex.getFiles(NeonFileType.INSTANCE, scope)

        for (vf in neonFiles) {
            if (vf.isPhpStan()) continue
            val psiFile = com.intellij.psi.PsiManager.getInstance(project).findFile(vf) as? NeonFile ?: continue
            val rootValue = psiFile.value
            if (rootValue !is NeonArray) continue
            val map = rootValue.map ?: continue
            val servicesValue = map["services"]
            if (servicesValue !is NeonArray) continue

            val keys = servicesValue.keys ?: continue
            for (key in keys.filterNotNull()) {
                if (key.keyText == serviceName) {
                    targets.add(key)
                }
            }
        }

        return if (targets.isEmpty()) null else targets.toTypedArray()
    }

    override fun getActionText(context: DataContext): String? = null
}
