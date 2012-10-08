
LOCAL_PATH := $(call my-dir)

all: pre_all
pre_all: uk_digitalsquid_internetrestore_jni_WpaControl.h uk_digitalsquid_internetrestore_jni_FileUtil.h

uk_digitalsquid_internetrestore_jni_WpaControl.h: $(LOCAL_PATH)/../bin/classes/uk/digitalsquid/internetrestore/jni/WpaControl.class
	cd $(LOCAL_PATH)/../bin/classes; \
	javah -d ../../jni uk.digitalsquid.internetrestore.jni.WpaControl

uk_digitalsquid_internetrestore_jni_FileUtil.h: $(LOCAL_PATH)/../bin/classes/uk/digitalsquid/internetrestore/jni/FileUtil.class
	cd $(LOCAL_PATH)/../bin/classes; \
	javah -d ../../jni uk.digitalsquid.internetrestore.jni.FileUtil

include $(CLEAR_VARS)

LOCAL_MODULE    := wpa
LOCAL_SRC_FILES := wpa_control.c ext/common.c ext/os_unix.c ext/wpa_ctrl.c
LOCAL_LDFLAGS := -lcutils -L$(LOCAL_PATH)
LOCAL_CFLAGS := -g -ggdb

include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE    := misc
LOCAL_SRC_FILES := file_util.c
LOCAL_CFLAGS := -g -ggdb

include $(BUILD_SHARED_LIBRARY)

# Build runner

runner/y.tab.c: runner/parser.y
	cd runner; yacc -d parser.y

runner/lex.yy.c: runner/lexer.l
	cd runner; flex lexer.l

include $(CLEAR_VARS)
LOCAL_MODULE := runner
LOCAL_SRC_FILES := runner/y.tab.c runner/lex.yy.c runner/main.c runner/proc.c
LOCAL_LDFLAGS := -llog
include $(BUILD_EXECUTABLE)
