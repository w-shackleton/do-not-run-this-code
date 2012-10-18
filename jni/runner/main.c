#include <stdio.h>
#include <string.h>

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
  
main()
{
	printf("STARTED\n");
	LOGT("Runner Started");
	fflush(stdout);
	// fflush(stderr);
	yyparse();
} 

