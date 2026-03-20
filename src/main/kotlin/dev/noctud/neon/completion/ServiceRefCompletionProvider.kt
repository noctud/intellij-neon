package dev.noctud.neon.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.ProcessingContext
import dev.noctud.neon.ext.isPhpStan
import dev.noctud.neon.file.NeonFileType
import dev.noctud.neon.lexer._NeonTypes
import dev.noctud.neon.psi.elements.NeonArray
import dev.noctud.neon.psi.elements.NeonFile

/**
 * Provides autocompletion for @service references.
 * Suggests named services defined in `services:` sections across project neon files.
 */
class ServiceRefCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        results: CompletionResultSet
    ) {
        val element = params.position
        if (element.node.elementType !== _NeonTypes.T_LITERAL) return
        if (!element.text.startsWith("@")) return

        val project = element.project
        val scope = GlobalSearchScope.projectScope(project)
        val neonFiles = FileTypeIndex.getFiles(NeonFileType.INSTANCE, scope)
        val services = mutableSetOf<String>()

        for (vf in neonFiles) {
            if (vf.isPhpStan()) continue
            val psiFile = com.intellij.psi.PsiManager.getInstance(project).findFile(vf) as? NeonFile ?: continue
            val rootValue = psiFile.value
            if (rootValue is NeonArray) {
                val map = rootValue.map ?: continue
                val servicesValue = map["services"]
                if (servicesValue is NeonArray) {
                    val keys = servicesValue.keys ?: continue
                    for (key in keys.filterNotNull()) {
                        val keyText = key.keyText ?: continue
                        if (keyText != "-") services.add(keyText)
                    }
                }
            }
        }

        for (service in services) {
            results.addElement(
                LookupElementBuilder.create(service)
                    .withPresentableText("@$service")
                    .withIcon(AllIcons.Nodes.Plugin)
                    .withTypeText("service")
            )
        }
    }
}
