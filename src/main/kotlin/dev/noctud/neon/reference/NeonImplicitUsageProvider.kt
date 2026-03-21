package dev.noctud.neon.reference

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.jetbrains.php.lang.psi.elements.PhpClass
import dev.noctud.neon.ext.isPhpStan
import dev.noctud.neon.file.NeonFileType

/**
 * Tells IntelliJ that a PHP class is implicitly used if it's referenced in any .neon file.
 * This prevents "Unused element" warnings on PHP classes that are only used in Neon configs.
 */
class NeonImplicitUsageProvider : ImplicitUsageProvider {
    override fun isImplicitUsage(element: PsiElement): Boolean {
        if (element !is PhpClass) return false

        val fqn = element.fqn?.trimStart('\\') ?: return false
        val shortName = fqn.substringAfterLast('\\')
        if (shortName.isEmpty()) return false

        val project = element.project
        val neonFiles = FileTypeIndex.getFiles(NeonFileType.INSTANCE, GlobalSearchScope.projectScope(project))

        for (vf in neonFiles) {
            if (vf.isPhpStan()) continue

            try {
                val content = vf.contentsToByteArray().toString(Charsets.UTF_8)
                if (content.contains(shortName) && content.contains(fqn)) {
                    return true
                }
            } catch (_: Exception) {
                continue
            }
        }

        return false
    }

    override fun isImplicitRead(element: PsiElement): Boolean = false
    override fun isImplicitWrite(element: PsiElement): Boolean = false
}
