package dev.noctud.neon.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.completion.PhpLookupElement
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import dev.noctud.neon.completion.insert.PhpReferenceInsertHandler.Companion.getInstance
import dev.noctud.neon.psi.elements.NeonEntity
import dev.noctud.neon.psi.elements.NeonScalar

/**
 * Complete class names
 */
class ClassCompletionProvider : CompletionProvider<CompletionParameters?>() {
    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        results: CompletionResultSet
    ) {
        val curr = params.position.originalElement
        // Don't suggest PHP classes inside %variable% or @service context
        if (curr.text.startsWith("%") || curr.text.startsWith("@")) return

        val incompleteKey = CompletionUtil.isIncompleteKey(curr)
        if (!incompleteKey && (curr.parent !is NeonEntity) && (curr.parent !is NeonScalar)) {
            return
        }

        val variants: MutableCollection<PhpNamedElement> = mutableSetOf()
        val phpIndex = PhpIndex.getInstance(curr.project)

        var prefixMatcher = results.prefixMatcher
        val prefix = prefixMatcher.prefix
        var namespace: String? = null
        if (prefix.contains("\\")) {
            val index = prefix.lastIndexOf("\\")
            namespace = prefix.take(index)
            prefixMatcher = prefixMatcher.cloneWithPrefix(prefix.substring(index + 1))
        }
        val classNames = phpIndex.getAllClassNames(prefixMatcher)

        for (name in classNames) {
            variants.addAll(filterClasses(phpIndex.getClassesByName(name), namespace))
            variants.addAll(filterClasses(phpIndex.getInterfacesByName(name), namespace))
        }

        // Add variants
        for (item in variants) {
            val lookupItem: PhpLookupElement = object : PhpLookupElement(item) {
                override fun getAllLookupStrings(): MutableSet<String?> {
                    val original = super.getAllLookupStrings()
                    val strings: MutableSet<String?> = HashSet<String?>(original.size + 1)
                    strings.addAll(original)
                    strings.add(this.namedElement!!.fqn.substring(1))
                    return strings
                }
            }
            lookupItem.handler = getInstance(incompleteKey)

            results.addElement(lookupItem)
        }
    }

    private fun filterClasses(classes: MutableCollection<PhpClass>, namespace: String?): MutableCollection<PhpClass> {
        var namespace = namespace ?: return classes
        namespace = "\\" + namespace + "\\"
        val result: MutableCollection<PhpClass> = ArrayList()
        for (cls in classes) {
            val classNs = cls.namespaceName
            if (classNs == namespace || classNs.startsWith(namespace)) {
                result.add(cls)
            }
        }
        return result
    }
}
