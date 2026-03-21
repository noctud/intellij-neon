package dev.noctud.neon.reference

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import dev.noctud.neon.ext.isPhpStan
import dev.noctud.neon.file.NeonFileType
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonArray
import dev.noctud.neon.psi.elements.NeonFile
import dev.noctud.neon.psi.elements.NeonKeyValPair

/**
 * Ctrl+Click navigation for %variable% parameters.
 * Navigates to the parameter definition in the `parameters:` section.
 */
class GoToParameterHandler : GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(element: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement>? {
        if (element == null) return null
        if (element.node.elementType !== _NeonTypes.T_LITERAL) return null

        // Find the %variable% at the cursor offset within the literal text
        val text = element.text
        val localOffset = offset - element.textRange.startOffset
        val varName = findVariableAtOffset(text, localOffset) ?: return null

        val file = element.containingFile ?: return null
        val isPhpStan = file.isPhpStan()

        val targets = mutableListOf<PsiElement>()

        if (isPhpStan) {
            // Search only in this file
            findParameterDefinition(element.containingFile as? NeonFile, varName)?.let { targets.add(it) }
        } else {
            // Search all non-phpstan neon files in the project
            val project = element.project
            val scope = GlobalSearchScope.projectScope(project)
            val neonFiles = FileTypeIndex.getFiles(NeonFileType.INSTANCE, scope)
            for (vf in neonFiles) {
                if (vf.isPhpStan()) continue
                val psiFile = com.intellij.psi.PsiManager.getInstance(project).findFile(vf) as? NeonFile ?: continue
                findParameterDefinition(psiFile, varName)?.let { targets.add(it) }
            }
        }

        // If not found in neon parameters, try .env files
        if (targets.isEmpty()) {
            findEnvDefinition(element.project, varName)?.let { targets.add(it) }
        }

        return if (targets.isEmpty()) null else targets.toTypedArray()
    }

    /**
     * Find a variable definition in .env files at the project root.
     * Returns the PsiElement for the line where the variable is defined.
     */
    private fun findEnvDefinition(project: com.intellij.openapi.project.Project, varName: String): PsiElement? {
        val basePath = project.basePath ?: return null
        val baseDir = LocalFileSystem.getInstance().findFileByPath(basePath) ?: return null

        for (child in baseDir.children) {
            if (child.name == ".env" || (child.name.startsWith(".env.") && !child.isDirectory)) {
                val psiFile = PsiManager.getInstance(project).findFile(child) ?: continue
                val text = psiFile.text
                val lines = text.lines()
                var offset = 0
                for (line in lines) {
                    if (line.startsWith("$varName=")) {
                        // Return the element at this offset
                        return psiFile.findElementAt(offset)
                    }
                    offset += line.length + 1 // +1 for newline
                }
            }
        }
        return null
    }

    /**
     * Find the %variable% name at a given offset within literal text.
     */
    private fun findVariableAtOffset(text: String, offset: Int): String? {
        var i = 0
        while (i < text.length) {
            if (text[i] == '%' && i + 1 < text.length) {
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

    /**
     * Find the PSI element where a parameter is defined.
     * For dot-notation like "doctrine.test123", navigates through nested keys.
     */
    private fun findParameterDefinition(file: NeonFile?, varName: String): PsiElement? {
        val rootValue = (file ?: return null).value
        if (rootValue !is NeonArray) return null

        val map = rootValue.map ?: return null
        val parametersValue = map["parameters"]
        if (parametersValue !is NeonArray) return null

        // Split by dot for nested parameter lookup
        val parts = varName.split(".")
        var current: NeonArray = parametersValue

        for ((index, part) in parts.withIndex()) {
            // Find the key-value pair with this key name
            val keys = current.keys ?: return null
            for (key in keys.filterNotNull()) {
                if (key.keyText == part) {
                    if (index == parts.lastIndex) {
                        // Found the final key — return it as the navigation target
                        return key
                    }
                    // Not the final part — descend into the value
                    val parent = key.parent
                    if (parent is NeonKeyValPair) {
                        val value = parent.value
                        if (value is NeonArray) {
                            current = value
                            break
                        }
                    }
                    return null // value is not an array, can't descend
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
