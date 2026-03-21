package dev.noctud.neon.reference

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.jetbrains.php.lang.psi.elements.PhpClass
import dev.noctud.neon.ext.isPhpStan
import dev.noctud.neon.file.NeonFileType

/**
 * Tells IntelliJ that a PHP class is implicitly used if it's referenced in any .neon file.
 * This prevents "Unused element" warnings on PHP classes that are only used in Neon configs.
 *
 * Uses a per-project cache of all FQNs found in neon files to avoid re-scanning
 * file contents for every PHP class in the project.
 */
class NeonImplicitUsageProvider : ImplicitUsageProvider {
    // Cache: project -> (timestamp, set of FQN strings found in neon files)
    private var cachedProject: Project? = null
    private var cachedFqns: Set<String> = emptySet()
    private var cachedTimestamp: Long = 0

    override fun isImplicitUsage(element: PsiElement): Boolean {
        if (element !is PhpClass) return false

        val fqn = element.fqn?.trimStart('\\') ?: return false
        if (fqn.isEmpty()) return false

        val project = element.project
        val fqns = getCachedFqns(project)

        return fqns.contains(fqn)
    }

    @Synchronized
    private fun getCachedFqns(project: Project): Set<String> {
        val now = System.currentTimeMillis()
        // Refresh cache every 10 seconds or on project change
        if (cachedProject === project && now - cachedTimestamp < 10_000) {
            return cachedFqns
        }

        cachedFqns = buildFqnSet(project)
        cachedProject = project
        cachedTimestamp = now
        return cachedFqns
    }

    private fun buildFqnSet(project: Project): Set<String> {
        val result = mutableSetOf<String>()
        val neonFiles = FileTypeIndex.getFiles(NeonFileType.INSTANCE, GlobalSearchScope.projectScope(project))

        for (vf in neonFiles) {
            if (vf.isPhpStan()) continue

            try {
                val content = vf.contentsToByteArray().toString(Charsets.UTF_8)
                // Extract anything that looks like a PHP FQN (contains \)
                FQN_PATTERN.findAll(content).forEach { match ->
                    val fqn = match.value.trimStart('@').trimStart('\\').substringBefore("::")
                    if (fqn.contains('\\')) {
                        result.add(fqn)
                    }
                }
            } catch (_: Exception) {
                continue
            }
        }

        return result
    }

    override fun isImplicitRead(element: PsiElement): Boolean = false
    override fun isImplicitWrite(element: PsiElement): Boolean = false

    companion object {
        // Matches FQNs like App\Model\Foo, @App\Model\Foo, App\Model\Foo::method
        private val FQN_PATTERN = Regex("@?[A-Za-z_][A-Za-z0-9_]*(?:\\\\[A-Za-z_][A-Za-z0-9_]*)+(?:::[a-zA-Z_]+)?")
    }
}
