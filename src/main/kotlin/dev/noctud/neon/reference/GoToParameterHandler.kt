package dev.noctud.neon.reference

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import dev.noctud.neon.ext.findParameterDefinition
import dev.noctud.neon.ext.getNeonFiles
import dev.noctud.neon.ext.isPhpStan
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonFile

/**
 * Ctrl+Click navigation for %variable% parameters.
 * Navigates to the parameter definition in the `parameters:` section.
 */
class GoToParameterHandler : GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(element: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement>? {
        if (element == null) return null
        if (element.node.elementType !== _NeonTypes.T_LITERAL && element.node.elementType !== _NeonTypes.T_STRING) return null

        val text = element.text
        val localOffset = offset - element.textRange.startOffset
        val varName = findVariableAtOffset(text, localOffset) ?: return null

        val file = element.containingFile ?: return null
        val targets = mutableListOf<PsiElement>()

        if (file.isPhpStan()) {
            (file as? NeonFile)?.findParameterDefinition(varName)?.let { targets.add(it) }
        } else {
            for (neonFile in element.project.getNeonFiles()) {
                neonFile.findParameterDefinition(varName)?.let { targets.add(it) }
            }
        }

        // Fallback: try .env files
        if (targets.isEmpty()) {
            findEnvDefinition(element.project, varName)?.let { targets.add(it) }
        }

        return if (targets.isEmpty()) null else targets.toTypedArray()
    }

    private fun findVariableAtOffset(text: String, offset: Int): String? {
        var i = 0
        while (i < text.length) {
            if (text[i] == '%' && i + 1 < text.length && text[i + 1] != '%') {
                val end = text.indexOf('%', i + 1)
                if (end > i + 1) {
                    val varName = text.substring(i + 1, end)
                    if (offset in i..end && VARIABLE_NAME.matches(varName)) {
                        return varName
                    }
                    i = end + 1
                    continue
                }
            }
            i++
        }
        return null
    }

    private fun findEnvDefinition(project: com.intellij.openapi.project.Project, varName: String): PsiElement? {
        val basePath = project.basePath ?: return null
        val baseDir = LocalFileSystem.getInstance().findFileByPath(basePath) ?: return null

        for (child in baseDir.children) {
            if (child.name == ".env" || (child.name.startsWith(".env.") && !child.isDirectory)) {
                val psiFile = PsiManager.getInstance(project).findFile(child) ?: continue
                val lines = psiFile.text.lines()
                var offset = 0
                for (line in lines) {
                    if (line.startsWith("$varName=")) {
                        return psiFile.findElementAt(offset)
                    }
                    offset += line.length + 1
                }
            }
        }
        return null
    }

    override fun getActionText(context: DataContext): String? = null

    companion object {
        private val VARIABLE_NAME = Regex("[a-zA-Z_][a-zA-Z0-9._-]*")
    }
}
