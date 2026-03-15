package dev.noctud.neon.psi.impl.elements

import com.intellij.lang.ASTNode
import dev.noctud.neon.psi.elements.NeonArray
import dev.noctud.neon.psi.elements.NeonKey
import dev.noctud.neon.psi.elements.NeonKeyValPair
import dev.noctud.neon.psi.elements.NeonValue
import java.util.regex.Pattern
import kotlin.math.max

class NeonArrayImpl(astNode: ASTNode) : NeonPsiElementImpl(astNode), NeonArray {
    override fun toString(): String {
        return "Neon array"
    }

    override val isList: Boolean
        get() {
            for (el in children) {
                if (el is NeonKeyValPair) {
                    return false
                }
            }
            return true
        }

    override val isHashMap: Boolean
        get() = !isList

    override val values: MutableList<NeonValue?>
        get() {
            val result =
                ArrayList<NeonValue?>()
            for (el in getChildren()) {
                if (el is NeonKeyValPair) {
                    result.add(el.value)
                } else {
                    result.add(if (el.children.size > 0) el.children[0] as NeonValue else null)
                }
            }
            return result
        }

    override val keys: MutableList<NeonKey?>
        get() {
            val result =
                ArrayList<NeonKey?>()
            for (el in getChildren()) {
                if (el is NeonKeyValPair) {
                    result.add(el.key)
                }
            }

            return result
        }

    override val map: HashMap<String?, NeonValue?>
        get() {
            val result: java.util.HashMap<String?, NeonValue?> =
                LinkedHashMap()
            var key = 0
            for (el in getChildren()) {
                if (el is NeonKeyValPair) {
                    val keyText = el.keyText ?: continue
                    result[keyText] = el.value
                    if (number.matcher(keyText).matches()) {
                        val keyInt = keyText.toInt()
                        key = max(keyInt + 1, key)
                    }
                } else {
                    val stringKey = (key++).toString()
                    result[stringKey] = if (el.children.size > 0) el.children[0] as NeonValue else null
                }
            }

            return result
        }

    companion object {
        private val number: Pattern = Pattern.compile("^\\d+$")
    }
}
