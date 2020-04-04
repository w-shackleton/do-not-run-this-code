#ifndef RUNNER_LOG_H
#define RUNNER_LOG_H

#ifdef ANDROID

#define TAG "inetrestore.native"
#define DEBUG

#include <android/log.h>
#define LOGT(string) __android_log_print(ANDROID_LOG_DEBUG, TAG, string)
#define LOG(string, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, string, args)

#ifdef DEBUG
#define DLOG(string) { printf("DEBUG: "string"\n"); LOGT("DEBUG: "string); }
#else
#define DLOG(string)
#endif

#else

#define LOGT(string) printf(string"\n")
#define LOG(string, args...) printf(string"\n", args)

#ifdef DEBUG
#define DLOG(string) { printf("DEBUG: "string"\n"); }
#else
#define DLOG(string)
#endif

#endif

#endif
