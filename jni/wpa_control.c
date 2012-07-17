// Source file for jni.WpaControl.java

#include "wpa_control.h"
#include "ext/wpa_ctrl.h"
#include "alog.h"

JNIEXPORT jint JNICALL Java_uk_digitalsquid_internetrestore_jni_WpaControl_openCtrl
  (JNIEnv *env, jclass class, jstring path, jstring local) {
	const char *cPath = (*env)->GetStringUTFChars(env, path, NULL);
	const char *cLocal = (*env)->GetStringUTFChars(env, local, NULL);
	LOGI("Opening socket at %s, with local socket %s.", cPath, cLocal);

	jint ptr = (jint) wpa_ctrl_open(cPath, cLocal);
	LOGI("wpa_ctrl interface returned is %d", ptr);
	(*env)->ReleaseStringUTFChars(env, path, cPath);
	return ptr;
}

JNIEXPORT void JNICALL Java_uk_digitalsquid_internetrestore_jni_WpaControl_closeCtrl
  (JNIEnv *env, jclass class, jint ptr) {
	wpa_ctrl_close((struct wpa_ctrl*)ptr);
}

JNIEXPORT jboolean JNICALL Java_uk_digitalsquid_internetrestore_jni_WpaControl_attach
  (JNIEnv *env, jclass class, jint ptr) {
	return wpa_ctrl_attach((struct wpa_ctrl*)ptr) == 0;
}

JNIEXPORT jboolean JNICALL Java_uk_digitalsquid_internetrestore_jni_WpaControl_detach
  (JNIEnv *env, jclass class, jint ptr) {
	return wpa_ctrl_detach((struct wpa_ctrl*)ptr) == 0;
}

JNIEXPORT jint JNICALL Java_uk_digitalsquid_internetrestore_jni_WpaControl_recv
  (JNIEnv *env, jclass class, jint ptr, jbyteArray buf) {
	int size = (*env)->GetArrayLength(env, buf);
	jbyte *array = (*env)->GetByteArrayElements(env, buf, NULL);

	size_t written;
	int result = wpa_ctrl_recv((struct wpa_ctrl*)ptr, array, &written);

	(*env)->ReleaseByteArrayElements(env, buf, array, 0);
	if(result == -1) return -1;
	return written;
}

JNIEXPORT jint JNICALL Java_uk_digitalsquid_internetrestore_jni_WpaControl_request
  (JNIEnv *env, jclass class, jint ptr, jbyteArray in, jbyteArray out) {
	int inSize = (*env)->GetArrayLength(env, in);
	int outSize = 0;

	jbyte *cIn = (*env)->GetByteArrayElements(env, in, NULL);
	jbyte *cOut = (*env)->GetByteArrayElements(env, out, NULL);

	int result = wpa_ctrl_request((struct wpa_ctrl*) ptr, cIn, inSize, cOut, &outSize, NULL);

	LOGV("Received %d bytes for request %s", outSize, cIn);

	(*env)->ReleaseByteArrayElements(env, in, cIn, 0);
	(*env)->ReleaseByteArrayElements(env, out, cOut, 0);
	if(result < 0) return result;
	return outSize;
}
