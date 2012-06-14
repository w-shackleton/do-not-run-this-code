
LOCAL_PATH := $(call my-dir)

all: uk_digitalsquid_internetrestore_jni_WpaControl.h

uk_digitalsquid_internetrestore_jni_WpaControl.h: $(LOCAL_PATH)/../bin/classes/uk/digitalsquid/internetrestore/jni/WpaControl.class
	cd $(LOCAL_PATH)/../bin/classes; \
	javah -d ../../jni uk.digitalsquid.internetrestore.jni.WpaControl

include $(CLEAR_VARS)

LOCAL_MODULE    := wpa
LOCAL_SRC_FILES := wpa_bridge.c

include $(BUILD_SHARED_LIBRARY)
