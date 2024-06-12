%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "exp_parser.tab.h"
extern void yyerror(const char *s);
%}

%%

0|[1-9][0-9]* { yylval.int_val = atoi(yytext); return INTEGER; }
[_]*[a-zA-Z][_a-zA-Z0-9]* { yylval.string_val = strdup(yytext); return VARIABLE; }

"+" { return PLUS; }
"-" { return MINUS; }
"*" { return MULTIPLY; }
"/" { return DIVIDE; }
"%" { return MODULO; }
"=" { return ASSIGN; }
\n { return EOL; }
[ \t] {}
. { fprintf(stderr, "Error: Unrecognized character '%s'\n", yytext); exit(EXIT_FAILURE); }
%%

int yywrap(void) {
    return 1;
}