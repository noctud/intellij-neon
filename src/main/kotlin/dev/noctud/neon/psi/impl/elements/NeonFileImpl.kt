package dev.noctud.neon.psi.impl.elements

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import dev.noctud.neon.NeonLanguage
import dev.noctud.neon.file.NeonFileType
import dev.noctud.neon.psi.elements.NeonFile
import dev.noctud.neon.psi.elements.NeonPsiElement

class NeonFileImpl(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, NeonLanguage.INSTANCE), NeonFile {
    override fun getFileType(): FileType {
        return NeonFileType.INSTANCE
    }

    override fun toString(): String {
        return "NeonFile: $name"
    }

    override val value: NeonPsiElement?
        get() {
            for (el in children) {
                if (el is NeonPsiElement) {
                    return el
                }
            }
            return null
        }

    override val neonFile: NeonFile
        get() = this
}
