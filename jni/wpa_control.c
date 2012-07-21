// Source file for jni.WpaControl.java

#include "wpa_control.h"
#include "ext/wpa_ctrl.h"
#include "alog.h"
#include <stdio.h>
#include <stdlib.h>

void hexdump(void *ptr, int buflen) {
	unsigned char *buf = (unsigned char*)ptr;
	int i, j;
	for (i=0; i<buflen; i+=16) {
		char *out = malloc(100);
		int pos = 0;
		memset(out, 0, 100);
		pos += sprintf(out + pos, "%06x: ", i);
		for (j=0; j<16; j++)
			if (i+j < buflen)
				pos += sprintf(out + pos, "%02x ", buf[i+j]);
			else
				pos += sprintf(out + pos, "   ");
		pos += sprintf(out + pos, " ");
		for (j=0; j<16; j++)
			if (i+j < buflen)
				pos += sprintf(out + pos, "%c", isprint(buf[i+j]) ? buf[i+j] : '.');
		pos += sprintf(out + pos, "\n");
		LOGV(out);
		free(out);
	}
}

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
	size_t inSize = (*env)->GetArrayLength(env, in);
	size_t outSize = (*env)->GetArrayLength(env, out);

	jbyte *cIn = (*env)->GetByteArrayElements(env, in, NULL);
	jbyte *cOut = (*env)->GetByteArrayElements(env, out, NULL);

	int result = wpa_ctrl_request((struct wpa_ctrl*) ptr, cIn, inSize, cOut, &outSize, NULL);

	LOGV("Received %d bytes for request %s(%d) - %d", outSize, cIn, inSize, result);

	(*env)->ReleaseByteArrayElements(env, in, cIn, 0);
	(*env)->ReleaseByteArrayElements(env, out, cOut, 0);
	if(result < 0) return result;
	return outSize;
}
