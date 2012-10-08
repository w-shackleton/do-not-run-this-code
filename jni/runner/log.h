#ifndef RUNNER_LOG_H
#define RUNNER_LOG_H

#ifdef ANDROID
#else
#define LOGT(string) printf(string"\n")
#define LOG(string, args...) printf(string"\n", args)
#endif

#endif
