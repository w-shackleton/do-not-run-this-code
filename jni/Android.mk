
LOCAL_PATH := $(call my-dir)

all: uk_digitalsquid_internetrestore_jni_WpaControl.h uk_digitalsquid_internetrestore_jni_FileUtil.h

uk_digitalsquid_internetrestore_jni_WpaControl.h: $(LOCAL_PATH)/../bin/classes/uk/digitalsquid/internetrestore/jni/WpaControl.class
	cd $(LOCAL_PATH)/../bin/classes; \
	javah -d ../../jni uk.digitalsquid.internetrestore.jni.WpaControl

uk_digitalsquid_internetrestore_jni_FileUtil.h: $(LOCAL_PATH)/../bin/classes/uk/digitalsquid/internetrestore/jni/FileUtil.class
	cd $(LOCAL_PATH)/../bin/classes; \
	javah -d ../../jni uk.digitalsquid.internetrestore.jni.FileUtil

include $(CLEAR_VARS)

LOCAL_MODULE    := wpa
LOCAL_SRC_FILES := wpa_control.c

include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE    := misc
LOCAL_SRC_FILES := file_util.c

include $(BUILD_SHARED_LIBRARY)