package dev.noctud.neon.ext

import com.intellij.openapi.vfs.VirtualFile
import dev.noctud.neon.file.NeonFileKind

val VirtualFile.neonFileKind: NeonFileKind
    get() = NeonFileKind.of(name)
