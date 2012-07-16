#include "uk_digitalsquid_internetrestore_jni_FileUtil.h"

#include <sys/stat.h>

#define EXEC_PERM ((S_IRUSR | S_IWUSR | S_IXUSR) | (S_IRGRP | S_IXGRP) | (S_IROTH | S_IXOTH)) /* 00755 */
#define PUBLIC_FILE_PERM	((S_IRUSR | S_IWUSR) | (S_IRGRP | S_IWGRP) | (S_IROTH | S_IWOTH)) /* 00777 */
#define PUBLIC_FOLDER_PERM	((S_IRUSR | S_IWUSR | S_IXUSR) | (S_IRGRP | S_IWGRP | S_IXGRP) | (S_IROTH | S_IWOTH | S_IXOTH)) /* 00777 */

JNIEXPORT void JNICALL Java_uk_digitalsquid_internetrestore_jni_FileUtil_setExecutable
  (JNIEnv *env, jclass class, jstring path) {
	const char *mpath = (*env)->GetStringUTFChars(env, path, NULL);

	chmod(mpath, EXEC_PERM);

	// Clean up
	(*env)->ReleaseStringUTFChars(env, path, mpath);
}

JNIEXPORT void JNICALL Java_uk_digitalsquid_internetrestore_jni_FileUtil_setPublicVisible
  (JNIEnv *env, jclass class, jstring path, jboolean isDirectory) {
	const char *mpath = (*env)->GetStringUTFChars(env, path, NULL);

	if(isDirectory)
		chmod(mpath, PUBLIC_FOLDER_PERM);
	else
		chmod(mpath, PUBLIC_FILE_PERM);

	// Clean up
	(*env)->ReleaseStringUTFChars(env, path, mpath);
}
