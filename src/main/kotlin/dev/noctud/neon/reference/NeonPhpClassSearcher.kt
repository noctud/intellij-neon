package dev.noctud.neon.reference

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.util.Processor
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import dev.noctud.neon.ext.isPhpStan
import dev.noctud.neon.file.NeonFileType
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.impl.elements.NeonScalarImpl

/**
 * Searches Neon files for references to PHP classes.
 * This makes "Find Usages" on a PHP class discover usages in .neon files,
 * and prevents the class from being marked as unused.
 */
class NeonPhpClassSearcher : QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters>(true) {
    override fun processQuery(params: ReferencesSearch.SearchParameters, consumer: Processor<in PsiReference>) {
        val target = params.elementToSearch
        if (target !is PhpNamedElement) return

        val fqn = target.fqn ?: return
        // Get the short class name to search for in file content
        val shortName = fqn.substringAfterLast('\\')
        if (shortName.isEmpty()) return

        val project = params.project
        val searchScope = params.effectiveSearchScope

        // Search Neon files in the scope
        val neonScope = GlobalSearchScope.getScopeRestrictedByFileTypes(
            if (searchScope is GlobalSearchScope) searchScope else GlobalSearchScope.projectScope(project),
            NeonFileType.INSTANCE
        )

        val psiManager = com.intellij.psi.PsiManager.getInstance(project)
        val neonFiles = com.intellij.psi.search.FileTypeIndex.getFiles(NeonFileType.INSTANCE, neonScope)
        val fqnWithoutLeadingSlash = fqn.trimStart('\\')

        for (vf in neonFiles) {
            if (vf.isPhpStan()) continue

            // Quick text check before parsing
            val content = vf.contentsToByteArray().toString(Charsets.UTF_8)
            if (!content.contains(shortName)) continue

            val psiFile = psiManager.findFile(vf) ?: continue

            // Walk the PSI tree looking for scalars that reference this class
            psiFile.accept(object : com.intellij.psi.PsiRecursiveElementWalkingVisitor() {
                override fun visitElement(element: PsiElement) {
                    if (element is NeonScalarImpl) {
                        val classFqn = element.classFqn
                        if (classFqn == fqnWithoutLeadingSlash) {
                            val ref = element.reference
                            if (ref != null) {
                                consumer.process(ref)
                            }
                        }
                    }
                    super.visitElement(element)
                }
            })
        }
    }
}
