package dev.noctud.neon.reference

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import dev.noctud.neon.lexer._NeonTypes
import java.io.File

/**
 * Ctrl+Click navigation for file paths in Neon files.
 * Resolves paths relative to the Neon file's directory, then project root.
 */
class GoToFileHandler : GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(element: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement>? {
        if (element == null) return null
        if (element.node.elementType !== _NeonTypes.T_LITERAL) return null

        var text = element.text
        if (!FILE_PATH.matches(text)) return null

        // Strip leading %variable% prefix (e.g., %currentWorkingDirectory%/tests/file.php → tests/file.php)
        text = text.replace(LEADING_VARIABLE, "")

        val neonFile = element.containingFile?.virtualFile ?: return null
        val project = element.project

        // Try relative to the neon file's directory, then project root
        val neonDir = neonFile.parent?.path ?: return null
        val basePath = project.basePath ?: return null
        val resolved = resolveFile(neonDir, text, project)
            ?: resolveFile(basePath, text, project)

        return if (resolved != null) arrayOf(resolved) else null
    }

    private fun resolveFile(basePath: String, relativePath: String, project: com.intellij.openapi.project.Project): PsiElement? {
        val fullPath = File(basePath, relativePath).canonicalPath
        val vf = LocalFileSystem.getInstance().findFileByPath(fullPath) ?: return null
        return PsiManager.getInstance(project).findFile(vf)
    }

    override fun getActionText(context: DataContext): String? = null

    companion object {
        private val FILE_PATH = Regex(".+\\.(?:php|neon)")
        private val LEADING_VARIABLE = Regex("^%[a-zA-Z_][a-zA-Z0-9._-]*%[/\\\\]?")
    }
}
