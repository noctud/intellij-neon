package dev.noctud.neon.completion.insert

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiDocumentManager
import com.jetbrains.php.lang.psi.elements.PhpClass

class PhpReferenceInsertHandler(private val incompleteKey: Boolean) : InsertHandler<LookupElement?> {
    override fun handleInsert(context: InsertionContext, lookupElement: LookupElement) {
        val obj = lookupElement.getObject()
        val classNamespace: String = if (obj is PhpClass) {
            obj.namespaceName
        } else {
            ""
        }

        if (!classNamespace.isEmpty()) {
            var fqn = classNamespace
            if (fqn.startsWith("\\")) {
                fqn = fqn.substring(1)
            }
            if (incompleteKey) {
                context.document.insertString(context.tailOffset, ": ")
                context.editor.caretModel.moveToOffset(context.editor.caretModel.offset + 2)
            }
            context.document.insertString(context.startOffset, fqn)
            PsiDocumentManager.getInstance(context.project).commitDocument(context.document)
        }
    }

    companion object {
        private val instances = arrayOf<PhpReferenceInsertHandler>(
            PhpReferenceInsertHandler(false), PhpReferenceInsertHandler(true)
        )

        @JvmStatic
        fun getInstance(incompleteKey: Boolean): PhpReferenceInsertHandler {
            return instances[if (incompleteKey) 1 else 0]
        }
    }
}
