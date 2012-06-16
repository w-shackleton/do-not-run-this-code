// Source file for jni.WpaControl.java

#include "wpa_control.h"
#include "ext/wpa_ctrl.h"

JNIEXPORT jlong JNICALL Java_uk_digitalsquid_internetrestore_jni_WpaControl_openCtrl
  (JNIEnv *env, jclass class, jstring path) {
	  char *cPath = (*env)->GetStringUTFChars(env, path, NULL);
	  // jlong ptr = (jlong) wpa_ctrl_open(cPath);
	  (*env)->ReleaseStringUTFChars(env, path, cPath);
	  // return ptr;
}
