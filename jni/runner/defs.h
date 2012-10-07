#ifndef DEFS_H
#define DEFS_H

#include <unistd.h>

typedef struct _argument {
	struct _argument *next;
	char *a;
} argument;

typedef struct _task {
	struct _task *next;
	char *name;
	pid_t pid;
	int input[2];
	argument *args;
} task;

task* tasks;

#endif
