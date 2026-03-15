package dev.noctud.neon.psi.impl.elements

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.project.Project
import com.intellij.psi.NavigatablePsiElement
import dev.noctud.neon.psi.elements.NeonFile
import dev.noctud.neon.psi.elements.NeonPsiElement

open class NeonPsiElementImpl(astNode: ASTNode) : ASTWrapperPsiElement(astNode), NavigatablePsiElement, NeonPsiElement {
    private var project: Project? = null
    private var file: NeonFile? = null

    override val neonFile: NeonFile?
        get() {
            if (file == null) {
                file = containingFile as? NeonFile
            }
            return file
        }

    override fun getProject(): Project {
        if (project == null) {
            project = super.getProject()
        }

        return project!!
    }
}
