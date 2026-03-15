package dev.noctud.neon.lexer

import com.intellij.psi.tree.IElementType
import dev.noctud.neon.NeonLanguage

class NeonTokenType(debugName: String) : IElementType(debugName, NeonLanguage.INSTANCE) {
    override fun toString(): String {
        return "[Neon] " + super.toString()
    }
}
