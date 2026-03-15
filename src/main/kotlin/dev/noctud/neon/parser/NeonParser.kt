package dev.noctud.neon.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import dev.noctud.neon.lexer.NeonTokenTypes
import kotlin.math.min

/**
 * Neon parser, convert tokens (output from lexer) into syntax tree
 */
class NeonParser : PsiParser, NeonTokenTypes, NeonElementTypes {
    private var myBuilder: PsiBuilder? = null
    private var myIndent = 0
    private var myIndentString: String? = null


    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        builder.setDebugMode(true)

        myBuilder = builder
        myIndent = 0
        myIndentString = ""

        // begin
        val fileMarker = myBuilder!!.mark()

        passEmpty() // process beginning of file
        parseValueOrArray(myIndent)
        while (!myBuilder!!.eof()) {
            if (myBuilder!!.tokenType !== NeonTokenTypes.NEON_INDENT) {
                myBuilder!!.error("unexpected token at end of file")
            }
            myBuilder!!.advanceLexer()
        }

        // end
        fileMarker.done(root)
        return builder.treeBuilt
    }

    private fun parseValue() {
        val currentToken = myBuilder!!.tokenType

        if (currentToken === NeonTokenTypes.NEON_INDENT || currentToken == null) {
            // no value -> null
        } else if (NeonTokenTypes.OPEN_BRACKET.contains(currentToken)) { // array
            val `val` = myBuilder!!.mark()
            parseInlineArray()
            if (myBuilder!!.tokenType === NeonTokenTypes.NEON_LPAREN) {
                parseEntity(`val`)
            } else {
                `val`.drop()
            }
        } else if (NeonTokenTypes.STRING_LITERALS.contains(currentToken)) {
            val `val` = myBuilder!!.mark()
            advanceLexer()
            `val`.done(NeonElementTypes.SCALAR_VALUE)
            if (myBuilder!!.tokenType === NeonTokenTypes.NEON_LPAREN) {
                parseEntity(`val`.precede())
            }
        } else {
            // dunno
            myBuilder!!.error("unexpected token $currentToken")
            advanceLexer()
        }
    }

    private fun parseArray(indent: Int, onlyBullets: Boolean = false) {
        while (myBuilder!!.tokenType != null && !NeonTokenTypes.CLOSING_BRACKET.contains(myBuilder!!.tokenType) && myIndent >= indent) {
            if (myIndent != indent) {
                myBuilder!!.error("bad indent")
            }
            val currentToken = myBuilder!!.tokenType
            if (onlyBullets && currentToken !== NeonTokenTypes.NEON_ARRAY_BULLET) {
                return
            }
            val nextToken = myBuilder!!.lookAhead(1)


            if (currentToken === NeonTokenTypes.NEON_ARRAY_BULLET && NeonTokenTypes.STRING_LITERALS.contains(nextToken) && NeonTokenTypes.ASSIGNMENTS.contains(
                    myBuilder!!.lookAhead(2)
                )
            ) { //key-after-bullet
                val markItem = myBuilder!!.mark()
                advanceLexer()
                val markArray = myBuilder!!.mark()
                val prevIndent = myIndentString
                parseKeyVal(indent + 2)
                passEmpty()
                if (("$prevIndent  ") == myIndentString) {
                    parseArray(indent + 2)
                }
                markArray.done(NeonElementTypes.ARRAY)
                markItem.done(NeonElementTypes.ITEM)
            } else if (NeonTokenTypes.ASSIGNMENTS.contains(nextToken)) { // key-val pair
                parseKeyVal(indent)
            } else if (currentToken === NeonTokenTypes.NEON_ARRAY_BULLET) {
                val markItem = myBuilder!!.mark()
                advanceLexer()
                parseValueOrArray(indent)
                markItem.done(NeonElementTypes.ITEM)
            } else {
                myBuilder!!.error(EXPECTED_ARRAY_ITEM)
                advanceLexer()
            }

            if (myBuilder!!.tokenType === NeonTokenTypes.NEON_INDENT) {
                advanceLexer()
            }
        }
    }

    private fun parseKeyVal(indent: Int) {
        myAssert(NeonTokenTypes.STRING_LITERALS.contains(myBuilder!!.tokenType), "Expected literal or string")

        val keyValPair = myBuilder!!.mark()
        parseKey()

        // key colon value
        myAssert(NeonTokenTypes.ASSIGNMENTS.contains(myBuilder!!.tokenType), "Expected assignment operator")
        advanceLexer()
        if (myBuilder!!.tokenType === NeonTokenTypes.NEON_INDENT && myBuilder!!.lookAhead(1) === NeonTokenTypes.NEON_ARRAY_BULLET) {
            //array-after-key syntax
            advanceLexer() //read indent
            val `val` = myBuilder!!.mark()
            parseArray(myIndent, myIndent == indent)
            `val`.done(NeonElementTypes.ARRAY)
        } else {
            // value
            parseValueOrArray(indent)
        }


        keyValPair.done(NeonElementTypes.KEY_VALUE_PAIR)
    }

    private fun parseValueOrArray(indent: Int) {
        var currentToken = myBuilder!!.tokenType
        if (currentToken === NeonTokenTypes.NEON_INDENT) {
            advanceLexer() // read indent
            currentToken = myBuilder!!.tokenType
            if (myIndent <= indent) {
                return  //null
            }
        }
        if (NeonTokenTypes.STRING_LITERALS.contains(currentToken) && myBuilder!!.lookAhead(1) === NeonTokenTypes.NEON_COLON || currentToken === NeonTokenTypes.NEON_ARRAY_BULLET) {
            val `val` = myBuilder!!.mark()
            parseArray(myIndent)
            `val`.done(NeonElementTypes.ARRAY)
        } else if (currentToken != null) {
            parseValue()
        }
    }

    private fun parseKey() {
        myAssert(NeonTokenTypes.STRING_LITERALS.contains(myBuilder!!.tokenType), "Expected literal or string")

        val key = myBuilder!!.mark()
        val scalar = myBuilder!!.mark()
        advanceLexer()
        scalar.done(NeonElementTypes.SCALAR_VALUE)
        key.done(NeonElementTypes.KEY)
    }

    private fun parseInlineArray(): Boolean {
        val currentToken = myBuilder!!.tokenType
        val `val` = myBuilder!!.mark()

        val closing: IElementType? = NeonTokenTypes.closingBrackets[currentToken]

        advanceLexer(currentToken) // opening bracket
        passEmpty()
        while (myBuilder!!.tokenType != null && !NeonTokenTypes.CLOSING_BRACKET.contains(myBuilder!!.tokenType)) {
            if (NeonTokenTypes.STRING_LITERALS.contains(myBuilder!!.tokenType) && NeonTokenTypes.ASSIGNMENTS.contains(myBuilder!!.lookAhead(1))) { // key-val pair
                val keyValPair = myBuilder!!.mark()
                parseKey()
                advanceLexer()
                parseValue()
                readInlineArraySeparator()

                keyValPair.done(NeonElementTypes.KEY_VALUE_PAIR)
            } else {
                parseValue()
                readInlineArraySeparator()
            }
            passEmpty()
        }
        val ok = advanceLexer(closing) // closing bracket
        `val`.done(NeonElementTypes.ARRAY)

        return ok
    }

    private fun readInlineArraySeparator() {
        if (myBuilder!!.tokenType === NeonTokenTypes.NEON_INDENT || myBuilder!!.tokenType === NeonTokenTypes.NEON_ITEM_DELIMITER) {
            advanceLexer()
        } else if (!NeonTokenTypes.CLOSING_BRACKET.contains(myBuilder!!.tokenType)) {
            myBuilder!!.error("Expected indent, delimiter or closing bracket")
        }
    }

    private fun parseEntity(value: PsiBuilder.Marker) {
        var v = value
        parseInlineArray()
        v.done(NeonElementTypes.ENTITY)
        if (NeonTokenTypes.STRING_LITERALS.contains(myBuilder!!.tokenType)) {
            v = v.precede()
            parseChainedEntity()
            v.done(NeonElementTypes.CHAINED_ENTITY)
        }
    }

    private fun parseChainedEntity() {
        while (true) {
            val inlineEntity = myBuilder!!.mark()
            val scalar = myBuilder!!.mark()
            advanceLexer()
            scalar.done(NeonElementTypes.SCALAR_VALUE)
            if (myBuilder!!.tokenType !== NeonTokenTypes.NEON_LPAREN) {
                //last entity without attributes
                inlineEntity.done(NeonElementTypes.ENTITY)
                return
            }
            parseInlineArray()
            inlineEntity.done(NeonElementTypes.ENTITY)
            if (myBuilder!!.tokenType === NeonTokenTypes.NEON_INDENT || myBuilder!!.tokenType == null) {
                return
            }
        }
    }


    /***  helpers  */
    /**
     * Go to next token; if there is more whitespace, skip to the last
     */
    private fun advanceLexer() {
        if (myBuilder!!.eof()) return

        do {
            val type = myBuilder!!.tokenType
            if (type === NeonTokenTypes.NEON_INDENT) {
                validateTabsSpaces()
                myIndent = myBuilder!!.tokenText!!.length
                if (myBuilder!!.tokenText!![0] == '\n') {
                    myIndent--
                }
            }

            myBuilder!!.advanceLexer()
        } while (myBuilder!!.tokenType === NeonTokenTypes.NEON_INDENT && myBuilder!!.lookAhead(1) === NeonTokenTypes.NEON_INDENT) // keep going if we're still indented
    }

    private fun advanceLexer(expectedToken: IElementType?): Boolean {
        if (myBuilder!!.tokenType === expectedToken) {
            advanceLexer()
            return true
        } else {
            myBuilder!!.error("unexpected token " + myBuilder!!.tokenType + ", expected " + expectedToken)
            return false
        }
    }

    /**
     * Check that only tabs or only spaces are used for indent
     */
    private fun validateTabsSpaces() {
        assert(myBuilder!!.tokenType === NeonTokenTypes.NEON_INDENT)
        val text = myBuilder!!.tokenText!!.replace("\n", "")
        if (text.isEmpty() && myBuilder!!.lookAhead(1) === NeonTokenTypes.NEON_INDENT) {
            return
        }
        if (text.isEmpty()) {
            myIndentString = ""
            return
        }
        val min = min(myIndentString!!.length, text.length)
        if (text.substring(0, min) != myIndentString!!.substring(0, min)) {
            myBuilder!!.error("Tab/space mixing")
        } else {
            myIndentString = text
        }
    }

    private fun myAssert(condition: Boolean, message: String?) {
        if (!condition) {
            myBuilder!!.error(message + ", got " + myBuilder!!.tokenType)
            advanceLexer()
        }
    }

    private fun passEmpty() {
        while (!myBuilder!!.eof() && (myBuilder!!.tokenType === NeonTokenTypes.NEON_INDENT || myBuilder!!.tokenType === NeonTokenTypes.NEON_UNKNOWN)) {
            advanceLexer()
        }
    }

    companion object {
        const val EXPECTED_ARRAY_ITEM: String = "expected key-val pair or array item"
    }
}
