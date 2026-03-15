/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.noctud.neon.lexer

import com.intellij.lexer.FlexLexer
import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType
import java.io.IOException

/**
 * neon flex has been broken by this commit:
 * https://github.com/JetBrains/intellij-community/commit/cb0e5b71e250c45bf0dcdc876461e12b4351b322
 *
 * Copy-pasted previous working version as a hotfix
 *
 * @author max
 */
open class FlexAdapter(flex: FlexLexer?) : LexerBase() {
    var flex: FlexLexer? = null
        private set
    private var myTokenType: IElementType? = null
    private var myText: CharSequence? = null

    private var myEnd = 0
    private var myState = 0

    init {
        this.flex = flex
    }

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        myText = buffer
        myEnd = endOffset
        flex!!.reset(myText, startOffset, endOffset, initialState)
        myTokenType = null
    }

    override fun getState(): Int {
        if (myTokenType == null) locateToken()
        return myState
    }

    override fun getTokenType(): IElementType? {
        if (myTokenType == null) locateToken()
        return myTokenType
    }

    override fun getTokenStart(): Int {
        if (myTokenType == null) locateToken()
        return flex!!.getTokenStart()
    }

    override fun getTokenEnd(): Int {
        if (myTokenType == null) locateToken()
        return flex!!.getTokenEnd()
    }

    override fun advance() {
        if (myTokenType == null) locateToken()
        myTokenType = null
    }

    override fun getBufferSequence(): CharSequence {
        return myText!!
    }

    override fun getBufferEnd(): Int {
        return myEnd
    }

    protected fun locateToken() {
        if (myTokenType != null) return
        try {
            myState = flex!!.yystate()
            myTokenType = flex!!.advance()
        } catch (e: IOException) { /*Can't happen*/
        } catch (e: Error) {
            // add lexer class name to the error
            val error = Error(flex!!.javaClass.getName() + ": " + e.message)
            error.setStackTrace(e.getStackTrace())
            throw error
        }
    }
}
