%{
#include <stdio.h>
#include "y.tab.h"
extern YYSTYPE yylval;

#define YY_INPUT(buf,result,max_size) \
		{ \
		int c = '*'; \
		size_t n; \
		for ( n = 0; n < max_size && \
			     (c = getc( yyin )) != EOF && c != '\n'; ++n ) \
			buf[n] = (char) c; \
		if ( c == '\n' ) \
			buf[n++] = (char) c; \
		if ( c == EOF && ferror( yyin ) ) \
			YY_FATAL_ERROR( "input in flex scanner failed" ); \
		result = n; \
		} \
\

%}
%%
quit			return TOKQUIT;
env			return TOKENV;
create			return TOKCREATE;
set			return TOKSET;
args			return TOKARGS;
start			return TOKSTART;
stop			return TOKSTOP;
list			return TOKLIST;
send			return TOKSEND;
sleep			return TOKSLEEP;
running			return TOKRUNNING;
[0-9]+			yylval=atoi(yytext); return NUMBER;
[a-zA-Z0-9/+_\.-]*	yylval=strdup(yytext); return TEXT;
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
