%{
#include <stdio.h>
#include "y.tab.h"
extern YYSTYPE yylval;
%}
%%
env			return TOKENV;
create			return TOKCREATE;
set			return TOKSET;
args			return TOKARGS;
start			return TOKSTART;
list			return TOKLIST;
[a-zA-Z0-9/+]*		yylval=strdup(yytext); return TEXT;
\"(\\.|[^"])*\"		{
	int newlen = strlen(yytext)-2;
	char *str = malloc(newlen+1);
	sscanf(yytext, "\"%[^\"]\"", str);
	yylval = str;
	return QTEXT;
	}
=			return EQ;
;			return SEMICOLON;
\n			/* ignore end of line */;
[ \t]+			/* ignore whitespace */;
%%
