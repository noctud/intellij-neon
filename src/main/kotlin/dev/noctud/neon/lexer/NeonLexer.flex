package dev.noctud.neon.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import static dev.noctud.neon.lexer._NeonTypes.*;

/**
 * @author Jan Dolecek
 * @author Jan Tvrdík
 * @author Michael Moravec
 */
%%

%class _NeonLexer
%implements FlexLexer
%public
%unicode
%function advance
%type IElementType

%{
    private void retryInState(int newState) {
        yybegin(newState);
        yypushback(yylength());
    }
%}

STRING = \'[^\'\n]*\'|\"(\\.|[^\"\\\n])*\"
COMMENT = \#.*
INDENT = \n[\t ]*
LITERAL_START = [^-:#\"\',=\[\]{}()\x00-\x20`]|[:-][!#$%&*\x2D-\x5C\x5E-\x7C~\xA0-\uFFFF]
WHITESPACE = [\t ]+

%states DEFAULT, IN_LITERAL, VYINITIAL, IN_MULTILINE_DQ, IN_MULTILINE_SQ

%%

<YYINITIAL> {

    {WHITESPACE} {
        return T_INDENT;
    }
    [^] {
        retryInState(DEFAULT);
    }
}

<DEFAULT> {
    "\"\"\"" / \n([^]*\n)?[ \t]*"\"\"\"" {
        yybegin(IN_MULTILINE_DQ);
        return T_STRING;
    }
    "'''" / \n([^]*\n)?[ \t]*"'''" {
        yybegin(IN_MULTILINE_SQ);
        return T_STRING;
    }
    {STRING} {
        return T_STRING;
    }

    "," { return T_ITEM_DELIMITER; }
    "=" { return T_ASSIGNMENT; }

    "(" { return T_LPAREN; }
    ")" { return T_RPAREN; }
    "{" { return T_LBRACE_CURLY; }
    "}" { return T_RBRACE_CURLY; }
    "[" { return T_LBRACE_SQUARE; }
    "]" { return T_RBRACE_SQUARE; }

    {COMMENT} {
        return T_COMMENT;
    }

    {INDENT} {
        return T_INDENT;
    }

    {LITERAL_START} {
        yybegin(IN_LITERAL);
        return T_LITERAL;
    }

    ":" { return T_COLON; }
    "-" { return T_ARRAY_BULLET; }

    {WHITESPACE} {
        return TokenType.WHITE_SPACE;
    }

    . {
        return T_UNKNOWN;
    }
}

<IN_LITERAL> {
    [^,:=\]})(\x00-\x20]+ {}
    [ \t]+[^#,:=\]})(\x00-\x20] {}
    ":" / [\x21-\x28*\x2D-\x5C\x5E-\x7C~\xA0-\uFFFF] { }
    ":" { retryInState(DEFAULT); }
    [^] { retryInState(DEFAULT); }
}

<IN_MULTILINE_DQ> {
    \n[ \t]*"\"\"\"" {
        yybegin(DEFAULT);
    }
    [^] {}
}
<IN_MULTILINE_SQ> {
    \n[ \t]*"'''" {
        yybegin(DEFAULT);
    }
    [^] {}
}
