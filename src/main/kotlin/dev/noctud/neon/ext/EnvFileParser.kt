package dev.noctud.neon.ext

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager

/** A `NAME=value` line in a .env file, [offset] points at NAME. */
data class EnvVariable(val name: String, val offset: Int)

/**
 * Parses .env files in the project root: .env, .env.local, .env.example, .env.development, .env.production, etc.
 *
 * Nette exposes them as the %env% array parameter, `$configurator->addDynamicParameters(['env' => getenv()])`,
 * so a variable is referenced as %env.NAME%, never as a bare %NAME%.
 */
object EnvFileParser {
    const val PARAMETER = "env"
    const val PREFIX = "$PARAMETER."

    private val DECLARATION = Regex("^[ \\t]*(?:export[ \\t]+)?([A-Za-z_][A-Za-z0-9_]*)[ \\t]*=", RegexOption.MULTILINE)
    private val VARIABLES = Key.create<CachedValue<List<EnvVariable>>>("neon.env.variables")

    fun isEnvParameter(varName: String): Boolean = varName == PARAMETER || varName.startsWith(PREFIX)

    fun findEnvFiles(project: Project): List<PsiFile> {
        val basePath = project.basePath ?: return emptyList()
        val baseDir = LocalFileSystem.getInstance().findFileByPath(basePath) ?: return emptyList()
        val psiManager = PsiManager.getInstance(project)
        return baseDir.children
            .filter { !it.isDirectory && (it.name == ".env" || it.name.startsWith(".env.")) }
            .mapNotNull { psiManager.findFile(it) }
    }

    fun collectEnvVariables(project: Project): Set<String> {
        val result = mutableSetOf<String>()
        for (file in findEnvFiles(project)) {
            variablesOf(file).mapTo(result) { it.name }
        }

        return result
    }

    /** Variables declared in [file], cached until the file changes. */
    fun variablesOf(file: PsiFile): List<EnvVariable> {
        return CachedValuesManager.getManager(file.project).getCachedValue(file, VARIABLES, {
            CachedValueProvider.Result.create(parse(file.viewProvider.contents), file)
        }, false)
    }

    fun parse(text: CharSequence): List<EnvVariable> {
        return DECLARATION.findAll(text).map {
            val name = it.groups[1]!!
            EnvVariable(name.value, name.range.first)
        }.toList()
    }
}
