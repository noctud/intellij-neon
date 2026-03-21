package dev.noctud.neon.ext

import com.intellij.psi.PsiElement
import dev.noctud.neon.psi.elements.NeonArray
import dev.noctud.neon.psi.elements.NeonFile
import dev.noctud.neon.psi.elements.NeonKeyValPair

/** Get a root-level section as NeonArray (e.g., "parameters", "services"). */
fun NeonFile.getRootSection(key: String): NeonArray? {
    val rootValue = value
    if (rootValue !is NeonArray) return null
    val map = rootValue.map ?: return null
    return map[key] as? NeonArray
}

/** Collect parameter names from this file's `parameters:` section. */
fun NeonFile.collectParameters(result: MutableSet<String>) {
    val parametersArray = getRootSection("parameters") ?: return
    collectParametersRecursive(parametersArray, "", result)
}

/** Collect service names from this file's `services:` section. */
fun NeonFile.collectServices(result: MutableSet<String>) {
    val servicesArray = getRootSection("services") ?: return
    val keys = servicesArray.keys ?: return
    for (key in keys.filterNotNull()) {
        val keyText = key.keyText ?: continue
        if (keyText != "-") result.add(keyText)
    }
}

/** Find the PsiElement for a parameter definition by name (supports dot-notation). */
fun NeonFile.findParameterDefinition(varName: String): PsiElement? {
    val parametersArray = getRootSection("parameters") ?: return null

    val parts = varName.split(".")
    var current: NeonArray = parametersArray

    for ((index, part) in parts.withIndex()) {
        val keys = current.keys ?: return null
        for (key in keys.filterNotNull()) {
            if (key.keyText == part) {
                if (index == parts.lastIndex) return key
                val parent = key.parent
                if (parent is NeonKeyValPair) {
                    val value = parent.value
                    if (value is NeonArray) {
                        current = value
                        break
                    }
                }
                return null
            }
        }
    }
    return null
}

private fun collectParametersRecursive(array: NeonArray, prefix: String, result: MutableSet<String>) {
    val keys = array.keys ?: return
    for (key in keys.filterNotNull()) {
        val keyText = key.keyText ?: continue
        val fullKey = if (prefix.isEmpty()) keyText else "$prefix.$keyText"
        result.add(fullKey)

        val parent = key.parent
        if (parent is NeonKeyValPair) {
            val value = parent.value
            if (value is NeonArray) {
                collectParametersRecursive(value, fullKey, result)
            }
        }
    }
}
