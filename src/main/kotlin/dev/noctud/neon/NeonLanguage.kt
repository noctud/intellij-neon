package dev.noctud.neon

import com.intellij.lang.Language

class NeonLanguage : Language("neon", MIME_TYPE) {
    override fun getDisplayName(): String {
        return "Neon"
    }

    companion object {
        @JvmField
        val INSTANCE: NeonLanguage = NeonLanguage()
        const val MIME_TYPE: String = "text/x-neon"
    }
}
