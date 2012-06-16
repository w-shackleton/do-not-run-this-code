#include "uk_digitalsquid_internetrestore_jni_FileUtil.h"

#include <sys/stat.h>

#define EXEC_PERM ((S_IRUSR | S_IWUSR | S_IXUSR) | (S_IRGRP | S_IXGRP) | (S_IROTH | S_IXOTH)) /* 00755 */

JNIEXPORT void JNICALL Java_uk_digitalsquid_internetrestore_jni_FileUtil_setExecutable
  (JNIEnv *env, jclass class, jstring path) {
	const char *mpath = (*env)->GetStringUTFChars(env, path, NULL);

	chmod(mpath, EXEC_PERM);

	// Clean up
	(*env)->ReleaseStringUTFChars(env, path, mpath);
}