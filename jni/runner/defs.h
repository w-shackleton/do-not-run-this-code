
typedef struct _argument {
	struct _argument *next;
	char *a;
} argument;

typedef struct _task {
	struct _task *next;
	char *name;
	argument *args;
} task;

task* tasks;
