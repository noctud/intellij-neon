package dev.noctud.neon.ext

import com.intellij.openapi.vfs.VirtualFile

fun VirtualFile.isPhpStan(): Boolean {
    return name.contains("phpstan", ignoreCase = true) && name.endsWith(".neon")
}
