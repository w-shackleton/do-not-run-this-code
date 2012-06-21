// Source file for jni.WpaControl.java

#include "wpa_control.h"
#include "ext/wpa_ctrl.h"

JNIEXPORT jint JNICALL Java_uk_digitalsquid_internetrestore_jni_WpaControl_openCtrl
  (JNIEnv *env, jclass class, jstring path) {
	  char *cPath = (*env)->GetStringUTFChars(env, path, NULL);
	  jint ptr = (jint) wpa_ctrl_open(cPath);
	  (*env)->ReleaseStringUTFChars(env, path, cPath);
	  return ptr;
}

JNIEXPORT void JNICALL Java_uk_digitalsquid_internetrestore_jni_WpaControl_closeCtrl
  (JNIEnv *env, jclass class, jint ptr) {
	wpa_ctrl_close((struct wpa_ctrl*)ptr);
}