#ifndef DEFS_H
#define DEFS_H

#include <unistd.h>
#include <stdio.h>

typedef struct _argument {
	struct _argument *next;
	char *a;
} argument;

typedef struct _task {
	struct _task *next;
	char *name;
	pid_t pid;
	pid_t helperpid;
	int input[2];
	FILE *inputfp;
	int output[2];
	argument *args;
} task;

task* tasks;

#endif
