# Note that NDK_MODULE_PATH must contain the patchfield parent directory. The
# makefile in the parent directory implicitly takes care of this.

LOCAL_PATH := $(call my-dir)

# Java Audio module.

include $(CLEAR_VARS)

LOCAL_MODULE := javamodule
LOCAL_LDLIBS := -llog
LOCAL_SRC_FILES := javamodule.c
LOCAL_STATIC_LIBRARIES := audiomodule buffersizeadapter
include $(BUILD_SHARED_LIBRARY)
$(call import-module,Patchfield/jni)

