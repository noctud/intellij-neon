package dev.noctud.neon.ext

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import dev.noctud.neon.file.NeonFileType
import dev.noctud.neon.psi.elements.NeonFile

/** Get all non-phpstan NeonFile PSI files in the project. */
fun Project.getNeonFiles(): List<NeonFile> {
    val scope = GlobalSearchScope.projectScope(this)
    val vFiles = FileTypeIndex.getFiles(NeonFileType.INSTANCE, scope)
    val psiManager = PsiManager.getInstance(this)
    return vFiles.mapNotNull { vf ->
        if (vf.isPhpStan()) null
        else psiManager.findFile(vf) as? NeonFile
    }
}

/** Collect all parameter names (with dot-notation) from all project neon files. */
fun Project.collectAllParameters(): Set<String> {
    val result = mutableSetOf<String>()
    for (file in getNeonFiles()) {
        file.collectParameters(result)
    }
    return result
}

/** Collect all named service keys from all project neon files. */
fun Project.collectAllServices(): Set<String> {
    val result = mutableSetOf<String>()
    for (file in getNeonFiles()) {
        file.collectServices(result)
    }
    return result
}

/** Find the PsiElement for service definitions by name across all project neon files. */
fun Project.findServiceDefinitions(serviceName: String): List<PsiElement> {
    val targets = mutableListOf<PsiElement>()
    for (file in getNeonFiles()) {
        val servicesArray = file.getRootSection("services") ?: continue
        val keys = servicesArray.keys ?: continue
        for (key in keys.filterNotNull()) {
            if (key.keyText == serviceName) targets.add(key)
        }
    }
    return targets
}
