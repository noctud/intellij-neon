package dev.noctud.neon.ext

import com.intellij.psi.PsiFile

fun PsiFile.isPhpStan(): Boolean {
    return virtualFile?.isPhpStan() ?: (name.contains("phpstan", ignoreCase = true) && name.endsWith(".neon"))
}
