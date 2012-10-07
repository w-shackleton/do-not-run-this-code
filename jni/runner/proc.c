#include "proc.h"

#include <stdio.h>
#include <unistd.h>

void startTask(task *task) {
	pipe(task->input);

	// Create process to send outputs to android log
	if((task->pid = fork()) == 0) {
		int out[2];
		pipe(out);
		pid_t child;
		// Create child process
		if((child = fork()) == 0) {
			dup2(task->input[1], 0);
			dup2(out[1], 1);
			dup2(out[1], 2);

			close(out[1]);

			printf("This is a test message\n");

			char text[100];
			scanf("%s", text);
			printf("Text: %s\n", text);

			_exit(0);
		}

		FILE *fp = fdopen(out[0], "r");
		char buf[0x1000];
		char *b = buf;
		while((b = fgets(b, 0x1000, fp))) {
			printf("Msg recv: \"%s\"\n", b);
		}
		printf("Ending\n");
		_exit(0);
	}
}
