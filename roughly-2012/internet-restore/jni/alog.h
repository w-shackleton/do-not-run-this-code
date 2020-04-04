#ifndef ALOG_H
#define ALOG_H

#include <android/log.h>

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, "inetrestore.jni", __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , "inetrestore.jni", __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , "inetrestore.jni", __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , "inetrestore.jni", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , "inetrestore.jni", __VA_ARGS__)

#endif
