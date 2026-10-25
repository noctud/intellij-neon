package dev.noctud.neon.ext

import com.intellij.psi.PsiFile
import dev.noctud.neon.file.NeonFileKind

val PsiFile.neonFileKind: NeonFileKind
    get() = NeonFileKind.of(name)

fun PsiFile.isPhpStan(): Boolean = neonFileKind == NeonFileKind.PHPSTAN

fun PsiFile.isTranslation(): Boolean = neonFileKind.isTranslation
