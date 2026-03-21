package dev.noctud.neon.ext

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem

/**
 * Parses .env files in the project root to extract variable names.
 * Supports: .env, .env.local, .env.example, .env.development, .env.production, etc.
 */
object EnvFileParser {
    private val ENV_LINE = Regex("^([A-Z_][A-Z0-9_]*)=.*$")

    fun collectEnvVariables(project: Project): Set<String> {
        val result = mutableSetOf<String>()
        val basePath = project.basePath ?: return result
        val baseDir = LocalFileSystem.getInstance().findFileByPath(basePath) ?: return result

        for (child in baseDir.children) {
            if (child.name == ".env" || (child.name.startsWith(".env.") && !child.isDirectory)) {
                try {
                    val content = String(child.contentsToByteArray(), Charsets.UTF_8)
                    for (line in content.lines()) {
                        val trimmed = line.trim()
                        if (trimmed.isEmpty() || trimmed.startsWith("#")) continue
                        val match = ENV_LINE.matchEntire(trimmed)
                        if (match != null) {
                            result.add(match.groupValues[1])
                        }
                    }
                } catch (_: Exception) {
                    // skip unreadable files
                }
            }
        }

        return result
    }
}
