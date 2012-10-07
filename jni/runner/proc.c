#include "proc.h"

#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>

void startTask(task *task) {
	pipe(task->input);
	pipe(task->output);

	fflush(stdout);
	fflush(stderr);

	if((task->helperpid = fork()) == 0) {
		// Create process to send outputs to android log
		FILE *fp = fdopen(task->output[0], "r");
		char buf[0x1000];
		char *b = buf;
		while((b = fgets(b, 0x1000, fp))) {
			printf("Msg recv: %s\n", b);
		}

		fclose(fp);
		close(task->output[0]);

		printf("Ending\n");
		_exit(0);
	}
	if((task->pid = fork()) == 0) {
		// Create child process
		dup2(task->input[1], 0);
		dup2(task->output[1], 1);
		dup2(task->output[1], 2);

		close(task->output[1]);

		// TODO: Exec here
		// Count args
		argument *args = task->args;
		int count = 0;
		while(args) {
			count++;
			args = args->next;
		}
		char *argArray[count+1];

		args = task->args;
		count = 0;
		while(args) {
			argArray[count++] = args->a;
			args = args->next;
		}
		argArray[count] = NULL;

		if(count) {
			execvp(argArray[0], argArray);
		}
		printf("Failed to run program: %s(%d)\n", strerror(errno), errno);

		_exit(0);
	}
}

void stopTask(task *task) {
	close(task->input[0]);
	close(task->input[1]);
	close(task->output[0]);
	close(task->output[1]);

	kill(task->pid, SIGTERM);
	sleep(1);
	kill(task->pid, SIGKILL);
	kill(task->helperpid, SIGTERM);
	task->pid = 0;
	task->helperpid = 0;
}
