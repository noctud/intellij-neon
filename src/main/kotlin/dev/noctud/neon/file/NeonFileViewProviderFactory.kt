package dev.noctud.neon.file

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.FileViewProviderFactory
import com.intellij.psi.PsiManager

class NeonFileViewProviderFactory : FileViewProviderFactory {
    override fun createFileViewProvider(
        virtualFile: VirtualFile,
        language: Language?,
        psiManager: PsiManager,
        physical: Boolean
    ): FileViewProvider {
        return NeonFileViewProvider(psiManager, virtualFile, physical)
    }
}
