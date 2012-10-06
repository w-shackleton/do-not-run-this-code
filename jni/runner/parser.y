%{
#include <stdio.h>
#include <string.h>
#include "defs.h"
 
%}

%token TOKENV TOKCREATE TOKSET TOKARGS TOKLIST TEXT QTEXT EQ QUOTE SEMICOLON

%%

commands:
        |        
        commands command SEMICOLON
        ;


command:
        env_add |
	task_create |
	task_set_args |
	task_list
        ;

env_add:
        TOKENV text EQ text
        {
		setenv($2, $4, 1);
        }
        ;

task_create:
	   TOKCREATE text
	   {
	   task* t = malloc(sizeof(task));
	   t->name = $2;
	   t->next = tasks;
	   t->args = NULL;
	   tasks = t;
	   }
	   ;

task_set_args:
	     TOKSET TOKARGS text args
	     {
	     	char* name = $3;
		argument *args = $4;

		task *t = tasks;
		while(t) {
			if(strcmp(name, t->name) == 0) {
				free(t->args);
				t->args = args;
				break;
			}
			t = t->next;
		}
	     };

args:
    {
    $$=NULL;
    }
    |
    arg args
    {
    	argument *arg = malloc(sizeof(argument));
	arg->a=$1;
	arg->next=$2;
	$$=arg;
    }
    ;

arg:
   text
   {
   	$$=$1;
   }
   ;

task_list:
	 TOKLIST
	 {
	 	task *ts = tasks;
		while(ts) {
			printf("Task %s\n", ts->name);
			argument *args = ts->args;
			printf("	Args:");
			while(args) {
				printf(" %s", args->a);
				args = args->next;
			}
			printf("\n");
			ts = ts->next;
		}
	 }
	 ;

text:
	QTEXT
	{
		$$=$1;
	}
	;
text:
	TEXT
	{
		$$=$1;
	}
	;
