package dev.noctud.neon.reference

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.FakePsiElement
import dev.noctud.neon.ext.EnvVariable
import javax.swing.Icon

/**
 * Navigation target for a variable declared in a .env file.
 * Without a .env plugin the file is plain text, so there is no PSI element at the declaration to point at.
 */
class EnvVariableTarget(private val file: PsiFile, private val variable: EnvVariable) : FakePsiElement() {
    override fun getParent(): PsiElement = file

    override fun getContainingFile(): PsiFile = file

    override fun getName(): String = variable.name

    override fun getTextOffset(): Int = variable.offset

    override fun getTextRange(): TextRange = TextRange.from(variable.offset, variable.name.length)

    override fun getLocationString(): String = file.name

    override fun getIcon(open: Boolean): Icon = AllIcons.Nodes.Variable

    override fun canNavigate(): Boolean = file.virtualFile != null

    override fun canNavigateToSource(): Boolean = canNavigate()

    override fun navigate(requestFocus: Boolean) {
        val virtualFile = file.virtualFile ?: return
        OpenFileDescriptor(file.project, virtualFile, variable.offset).navigate(requestFocus)
    }
}
