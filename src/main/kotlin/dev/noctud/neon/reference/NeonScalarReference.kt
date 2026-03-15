package dev.noctud.neon.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.jetbrains.php.PhpIndex
import dev.noctud.neon.psi.elements.NeonScalar

class NeonScalarReference(element: NeonScalar, rangeInElement: TextRange, soft: Boolean, val text: String) : PsiReferenceBase<PsiElement>(element, rangeInElement, soft) {
    override fun resolve(): PsiElement? {
        return PhpIndex.getInstance(element.project).getAnyByFQN(text).firstOrNull()
    }
}
