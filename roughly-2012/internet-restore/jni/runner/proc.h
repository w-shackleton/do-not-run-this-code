#ifndef PROC_H
#define PROC_H

#include "defs.h"

void startTask(task *task);
void stopTask(task *task);
void sendMessage(task *task, char *msg);

#endif
