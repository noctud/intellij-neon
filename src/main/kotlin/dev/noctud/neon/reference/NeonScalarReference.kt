package dev.noctud.neon.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import dev.noctud.neon.psi.elements.NeonScalar

class NeonScalarReference(element: NeonScalar, rangeInElement: TextRange, soft: Boolean, val text: String) : PsiReferenceBase<PsiElement>(element, rangeInElement, soft) {
    override fun resolve(): PsiElement? {
        val phpIndex = PhpIndex.getInstance(element.project)
        var classes = phpIndex.getAnyByFQN(text)
        if (classes.isEmpty() && !text.startsWith("\\")) {
            classes = phpIndex.getAnyByFQN("\\$text")
        }
        return classes.firstOrNull()
    }

    override fun handleElementRename(newName: String): PsiElement {
        val scalar = element as? NeonScalar ?: return element
        val leaf = scalar.firstChild as? LeafPsiElement ?: return element

        val oldText = leaf.text
        val oldFqn = text
        val lastSep = oldFqn.lastIndexOf('\\')

        // Build new FQN: replace the short class name portion with newName
        val newFqn = if (lastSep >= 0) {
            oldFqn.substring(0, lastSep + 1) + newName
        } else {
            newName
        }

        val newText = oldText.replace(oldFqn, newFqn)
        leaf.replaceWithText(newText)
        return element
    }

    override fun bindToElement(newTarget: PsiElement): PsiElement {
        if (newTarget !is PhpNamedElement) return element
        val scalar = element as? NeonScalar ?: return element
        val leaf = scalar.firstChild as? LeafPsiElement ?: return element

        // Get the new FQN from the moved/renamed PHP element
        var newFqn = newTarget.fqn
        // Strip leading backslash — Neon uses FQNs without it
        if (newFqn.startsWith("\\")) {
            newFqn = newFqn.substring(1)
        }

        val oldText = leaf.text
        val newText = oldText.replace(text, newFqn)
        leaf.replaceWithText(newText)
        return element
    }
}
