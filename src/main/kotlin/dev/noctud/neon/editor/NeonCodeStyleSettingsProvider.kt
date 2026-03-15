package dev.noctud.neon.editor

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleConfigurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider
import dev.noctud.neon.NeonLanguage

class NeonCodeStyleSettingsProvider : CodeStyleSettingsProvider() {
    override fun getLanguage(): Language = NeonLanguage.INSTANCE

    @Deprecated("Deprecated in Java")
    override fun createConfigurable(settings: CodeStyleSettings, originalSetting: CodeStyleSettings): CodeStyleConfigurable {
        return object : CodeStyleAbstractConfigurable(settings, originalSetting, configurableDisplayName) {
            override fun createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel {
                return object : TabbedLanguageCodeStylePanel(NeonLanguage.INSTANCE, currentSettings, settings) {
                    override fun initTabs(settings: CodeStyleSettings?) {
                        addIndentOptionsTab(settings)
                    }
                }
            }

            override fun getHelpTopic(): String {
                return "reference.settingsdialog.codestyle.neon" // what is this?
            }
        }
    }

    override fun getConfigurableDisplayName(): String {
        return NeonLanguage.INSTANCE.displayName
    }
}
