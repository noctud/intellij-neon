package dev.noctud.neon.completion.schema

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.elements.*
import java.io.File

/**
 * Reads schema definition and finds available keys and values for completion
 */
class SchemaProvider {
    /**
     * Public interface, wraps native impl in cache
     */
    @Synchronized
    fun getKnownTypes(project: Project): MutableMap<String?, MutableCollection<String?>?> {
        // TODO: add cache
        return parseTypes(project)
    }

    /**
     * Reads annotations and metadata and produces a cacheable map
     */
    private fun parseTypes(project: Project): MutableMap<String?, MutableCollection<String?>?> {
        val map: MutableMap<String?, MutableCollection<String?>?> = mutableMapOf()
        val variables = getVariables(project, "CONFIG_KEYS")
        for (variable in variables) {
            if (NAMESPACE_NAME != variable.namespaceName) continue
            val parent = variable.parent

            if (parent is AssignmentExpression) {
                val value = parent.value
                if (value is ArrayCreationExpression) {
                    val elements = value.hashElements
                    parseTypes2(map, elements, "")
                }
            }
        }
        return map
    }

    private fun parseTypes2(
        map: MutableMap<String?, MutableCollection<String?>?>,
        elements: Iterable<ArrayHashElement>,
        parent: String
    ) {
        var types = map[parent]
        if (types == null) {
            types = ArrayList()
            map[parent] = types
        }

        for (element in elements) {
            val key = element.key
            if (key is StringLiteralExpression) {
                // key
                val keyName = key.contents
                types.add(keyName)

                val fullKeyName = if (parent.isNotEmpty()) ("$parent.$keyName") else keyName


                // value
                val `val` = element.value
                if (`val` is ArrayCreationExpression) { // recursive
                    val subElements = `val`.hashElements
                    parseTypes2(map, subElements, fullKeyName)
                } else if (`val` is FieldReference) { // reference to a field, where it's defined
                    val classFqn = (`val`.classReference as ClassReference).fqn
                    for (phpClass in PhpIndex.getInstance(element.project).getClassesByFQN(classFqn)) {
                        val field = phpClass.findFieldByName(`val`.nameCS, false)
                        if (field!!.defaultValue is ArrayCreationExpression) {
                            val subElements = (field.defaultValue as ArrayCreationExpression).hashElements
                            parseTypes2(map, subElements, fullKeyName)
                        }
                    }
                } else { // get value type
                    parseValueType(`val`)

                    // try annotation
                    var el2: PsiElement? = element
                    while (el2 != null && (el2 is LeafPsiElement && el2.elementType === PhpTokenTypes.opCOMMA || el2 is PsiWhiteSpace)) {
                        el2 = el2.nextSibling
                    }
                    if (el2 is PsiComment) {
                        println("Comment for " + fullKeyName + ": " + el2.text)
                    }
                }
            }
        }
    }

    private fun parseValueType(`val`: PhpPsiElement?) {
        val x = 1
    }

    private fun getVariables(project: Project, key: String?): MutableCollection<Variable> {
        return PhpIndex.getInstance(project).getVariablesByName(key)
    }

    private fun getMetaFile(project: Project): PsiFile? {
        val metaFile = LocalFileSystem.getInstance()
            .findFileByPath(project.basePath + File.separatorChar + ".phpstorm.meta.php")
        return if (metaFile != null) PsiManager.getInstance(project).findFile(metaFile) else null
    }

    companion object {
        const val NAMESPACE_NAME: String = "\\NEON_META\\"
    }
}
