#ifndef MANDEL_CL_UTIL_H
#define MANDEL_CL_UTIL_H

#ifdef __cplusplus
extern "C" {
#endif

void checkCLError(int error, const char *file, int line);
const char* oclErrorString(int error);
#define CHECK_ERROR(_error) if((_error)) checkCLError((_error), __FILE__, __LINE__)

void hexdump(void *ptr, int buflen);

#ifdef __cplusplus
}
#endif

#endif
