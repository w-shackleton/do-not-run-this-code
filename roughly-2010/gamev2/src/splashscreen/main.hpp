#ifndef SPLASH_MAIN_H
#define SPLASH_MAIN_H
int splashMain(int argc, char **argv);
enum
{
	REASON_START = 1,
	REASON_QUIT
};

extern int splashReason;
#endif
