// Source file for jni.WpaControl.java

#include "wpa_control.h"
#include "ext/wpa_ctrl.h"

JNIEXPORT jint JNICALL Java_uk_digitalsquid_internetrestore_jni_WpaControl_openCtrl
  (JNIEnv *env, jclass class, jstring path) {
	  const char *cPath = (*env)->GetStringUTFChars(env, path, NULL);
	  jint ptr = (jint) wpa_ctrl_open(cPath);
	  (*env)->ReleaseStringUTFChars(env, path, cPath);
	  return ptr;
}

JNIEXPORT void JNICALL Java_uk_digitalsquid_internetrestore_jni_WpaControl_closeCtrl
  (JNIEnv *env, jclass class, jint ptr) {
	wpa_ctrl_close((struct wpa_ctrl*)ptr);
}

JNIEXPORT jboolean JNICALL Java_uk_digitalsquid_internetrestore_jni_WpaControl_attach
  (JNIEnv *env, jclass class, jint ptr) {
	wpa_ctrl_attach((struct wpa_ctrl*)ptr) == 0;
}

JNIEXPORT jboolean JNICALL Java_uk_digitalsquid_internetrestore_jni_WpaControl_detach
  (JNIEnv *env, jclass class, jint ptr) {
	wpa_ctrl_detach((struct wpa_ctrl*)ptr) == 0;
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
