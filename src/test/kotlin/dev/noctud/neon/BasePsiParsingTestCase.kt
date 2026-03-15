package dev.noctud.neon

import com.intellij.lang.ParserDefinition
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.psi.PsiFile
import com.intellij.testFramework.ParsingTestCase
import com.intellij.testFramework.TestDataFile
import org.jetbrains.annotations.NonNls
import java.io.File
import java.io.IOException

abstract class BasePsiParsingTestCase protected constructor(parserDefinition: ParserDefinition) :
    ParsingTestCase("", "neon", parserDefinition) {
    @Throws(IOException::class)
    override fun loadFile(@TestDataFile name: @NonNls String): String {
        return FileUtil.loadFile(File(myFullDataPath, name), CharsetToolkit.UTF8, true)
    }

    @Throws(IOException::class)
    protected fun parseFile(fileName: String): PsiFile? {
        return parseFile(fileName, loadFile(fileName))
    }
}
