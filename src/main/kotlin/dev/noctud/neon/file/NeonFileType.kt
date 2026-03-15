package dev.noctud.neon.file

import com.intellij.openapi.fileTypes.LanguageFileType
import dev.noctud.neon.Neon
import dev.noctud.neon.NeonIcons
import dev.noctud.neon.NeonLanguage
import javax.swing.Icon

class NeonFileType protected constructor() : LanguageFileType(NeonLanguage.INSTANCE) {
    override fun getName(): String {
        return Neon.LANGUAGE_NAME
    }

    override fun getDescription(): String {
        return Neon.LANGUAGE_DESCRIPTION
    }

    override fun getDefaultExtension(): String {
        return DEFAULT_EXTENSION
    }

    override fun getIcon(): Icon {
        return NeonIcons.FILETYPE_ICON
    }

    companion object {
        val INSTANCE: NeonFileType = NeonFileType()
        const val DEFAULT_EXTENSION: String = "neon"

        /**
         * All extensions which are associated with this plugin.
         */
        val extensions: Array<String?> = arrayOf<String?>(
            DEFAULT_EXTENSION
        )
    }
}

