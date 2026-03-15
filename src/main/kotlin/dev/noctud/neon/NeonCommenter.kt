package dev.noctud.neon

import com.intellij.lang.Commenter

class NeonCommenter : Commenter {
    override fun getLineCommentPrefix(): String {
        return "#"
    }

    override fun getBlockCommentPrefix(): String? {
        return null
    }

    override fun getBlockCommentSuffix(): String? {
        return null
    }

    override fun getCommentedBlockCommentPrefix(): String? {
        return null
    }

    override fun getCommentedBlockCommentSuffix(): String? {
        return null
    }
}
