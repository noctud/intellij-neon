package dev.noctud.neon.editor

import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider
import dev.noctud.neon.NeonLanguage
import dev.noctud.neon.psi.elements.NeonEntity
import dev.noctud.neon.psi.elements.NeonKeyValPair

/**
 * Breadcrumbs info about which section are we editing now (just above the editor, below tabs)
 */
class NeonBreadcrumbsInfoProvider : BreadcrumbsProvider {
    private val ourLanguages = arrayOf<Language?>(NeonLanguage.INSTANCE)

    override fun getLanguages(): Array<Language?> {
        return ourLanguages
    }

    override fun acceptElement(e: PsiElement): Boolean {
        return (e is NeonKeyValPair) || (e is NeonEntity)
    }

    override fun getElementInfo(e: PsiElement): String {
        when (e) {
            is NeonKeyValPair -> {
                return e.keyText!!
            }

            is NeonEntity -> {
                val name = e.name
                return name ?: "??"
            }

            else -> {
                return "??"
            }
        }
    }

    override fun getElementTooltip(e: PsiElement): String? {
        return e.toString()
    }
}
