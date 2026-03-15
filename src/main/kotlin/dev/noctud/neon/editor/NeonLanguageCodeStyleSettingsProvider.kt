package dev.noctud.neon.editor

import com.intellij.application.options.IndentOptionsEditor
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.IndentOptions
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import dev.noctud.neon.NeonLanguage

/**
 * Code style settings (tabs etc)
 */
class NeonLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
    override fun customizeDefaults(commonSettings: CommonCodeStyleSettings, indentOptions: IndentOptions) {
        indentOptions.INDENT_SIZE = 4
        //		indentOptions.USE_TAB_CHARACTER = true;
        indentOptions.SMART_TABS = false
        super.customizeDefaults(commonSettings, indentOptions)
    }

    override fun getIndentOptionsEditor(): IndentOptionsEditor? {
        return IndentOptionsEditor()
    }

    override fun getLanguage(): Language {
        return NeonLanguage.INSTANCE
    }

    override fun getCodeSample(settingsType: SettingsType): String? {
        return "product:\n    name: Neon\n    version: 4\n    vendor: juzna.cz\n    url: \"http://blog.juzna.cz\""
    }
}
