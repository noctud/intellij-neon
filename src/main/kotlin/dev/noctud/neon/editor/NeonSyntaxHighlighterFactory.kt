package dev.noctud.neon.editor

import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import dev.noctud.neon.ext.neonFileKind

class NeonSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(project: Project?, virtualFile: VirtualFile?): SyntaxHighlighter {
        return NeonSyntaxHighlighter(translation = virtualFile?.neonFileKind?.isTranslation == true)
    }
}
