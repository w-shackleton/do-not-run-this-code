%{
#include <stdio.h>
#include <string.h>
#include "defs.h"
#include "proc.h"
 
%}

%token TOKENV TOKCREATE TOKSET TOKARGS TOKLIST TOKSTART TOKSTOP TOKSEND TOKSLEEP TOKRUNNING TEXT QTEXT EQ QUOTE SEMICOLON NUMBER

%%

commands:
        |        
        commands command SEMICOLON
        ;


command:
        env_add |
	task_create |
	task_set_args |
	task_list |
	task_start |
	task_stop |
	task_send |
	task_running |
	sleep
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

task_start:
	  TOKSTART text
	  {
	     	char* name = $2;

		task *t = tasks;
		while(t) {
			if(strcmp(name, t->name) == 0) {
				startTask(t);
				break;
			}
			t = t->next;
		}
	  }
	  ;
task_stop:
	 TOKSTOP text
	 {
	     	char* name = $2;

		task *t = tasks;
		while(t) {
			if(strcmp(name, t->name) == 0) {
				stopTask(t);
				break;
			}
			t = t->next;
		}
	 }
	 ;
	 
task_running:
	TOKRUNNING text
	{
	    char* name = $2;

		task *t = tasks;
		while(t) {
			if(strcmp(name, t->name) == 0) {
				// Send test signal
				if(t->pid && kill(t->pid, 0)) {
					printf("RUNNING\n");
					return;
				}
			}
			t = t->next;
		}
		printf("NOTRUNNING\n");
	}
	;

task_send:
	 TOKSEND text text
	 {
	     	char* name = $2;

		task *t = tasks;
		while(t) {
			if(strcmp(name, t->name) == 0) {
				sendMessage(t, $3);
				break;
			}
			t = t->next;
		}
	 };

sleep:
	  TOKSLEEP NUMBER
	  {
	  	sleep($2);
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

text:
	NUMBER
	{
		$$=$1;
	}
	;