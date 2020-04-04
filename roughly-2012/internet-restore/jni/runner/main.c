#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "defs.h"
#include "log.h"

void yyerror(const char *str)
{
        fprintf(stderr,"Input error: %s\n",str);
        LOG("Input error: %s", str);
}
 
int yywrap()
{
        return 1;
} 

extern FILE *yyin;
  
main(int argc, char **argv)
{
	printf("STARTED\n");
	LOGT("Runner Started");
	fflush(stdout);
	// fflush(stderr);

	if(argc > 1) {
		yyin = fopen(argv[1], "r");
	}

	yyparse();

	if(argc > 1) {
		fclose(yyin);
	}
} 

