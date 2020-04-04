#include "proc.h"

#include "log.h"

#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>

void startTask(task *task) {
	pipe(task->input);
	pipe(task->output);

	if(!(task->inputfp = fdopen(task->input[1], "w"))) {
		printf("Failed to fdopen: %s(%d)\n", strerror(errno), errno);
	}

	fflush(stdout);
	fflush(stderr);

	if((task->helperpid = fork()) == 0) {
		// Create process to send outputs to android log
		FILE *fp = fdopen(task->output[0], "r");
		char buf[0x1000];
		char *b = buf;
		while((b = fgets(b, 0x1000, fp))) {
			LOG("%s: %s", task->name, b);
		}

		fclose(fp);
		close(task->output[0]);

		LOGT("Ending");
		_exit(0);
	}
	if((task->pid = fork()) == 0) {
		// Create child process
		dup2(task->input[0], 0);
		dup2(task->output[1], 1);
		dup2(task->output[1], 2);

		close(task->output[1]);

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

		if(argArray[0]) LOG("Running %s", argArray[0]);

		if(count) {
			execvp(argArray[0], argArray);
		}
		LOG("Failed to run program: %s(%d)", strerror(errno), errno);

		kill(task->helperpid, SIGTERM);

		_exit(0);
	}
}

void stopTask(task *task) {
	if(task->pid) {
		if(task->inputfp) fclose(task->inputfp);
		close(task->input[0]);
		close(task->input[1]);
		close(task->output[0]);
		close(task->output[1]);
		kill(task->pid, SIGTERM);
		sleep(1);
		kill(task->pid, SIGKILL);
	}

	if(task->helperpid)
		kill(task->helperpid, SIGTERM);
	task->pid = 0;
	task->helperpid = 0;
}

void sendMessage(task *task, char *msg) {
	if(task->pid && fprintf(task->inputfp, "%s\n", msg) != EOF) {
		fflush(task->inputfp);
	} else {
		LOG("Failed to send message to stopped task %s", task->name);
	}
}
