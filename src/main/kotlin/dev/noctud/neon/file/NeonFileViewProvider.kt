package dev.noctud.neon.file

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.SingleRootFileViewProvider

/**
 * disables incremental reparsing
 */
class NeonFileViewProvider : SingleRootFileViewProvider {
    /*** boilerplate code  */
    constructor(psiManager: PsiManager, virtualFile: VirtualFile) : super(psiManager, virtualFile)

    constructor(psiManager: PsiManager, virtualFile: VirtualFile, b: Boolean) : super(psiManager, virtualFile, b)

    constructor(psiManager: PsiManager, virtualFile: VirtualFile, b: Boolean, language: Language) : super(
        psiManager,
        virtualFile,
        b,
        language
    )

    override fun supportsIncrementalReparse(language: Language): Boolean {
        return false
    }
}
