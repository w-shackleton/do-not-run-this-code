#ifndef RUNNER_LOG_H
#define RUNNER_LOG_H

#ifdef ANDROID

#define TAG "inetrestore.native"

#include <android/log.h>
#define LOGT(string) __android_log_print(ANDROID_LOG_DEBUG, TAG, string)
#define LOG(string, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, string, args)

#else

#define LOGT(string) printf(string"\n")
#define LOG(string, args...) printf(string"\n", args)

#endif

#endif
