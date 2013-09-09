# Note that NDK_MODULE_PATH must contain the patchfield parent directory. The
# makefile in PatchfieldPcmSample implicitly takes care of this.

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := pfcsound
LOCAL_C_INCLUDES := ../../csound-csound6-git/include
LOCAL_SRC_FILES := pfcsound.c
LOCAL_STATIC_LIBRARIES := audiomodule buffersizeadapter
LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
include $(BUILD_SHARED_LIBRARY)
$(call import-module,Patchfield/jni)
