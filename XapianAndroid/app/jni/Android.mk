LOCAL_PATH := $(call my-dir)

# static library info
LOCAL_MODULE := libxapian
LOCAL_SRC_FILES := ../prebuild/libxapian.a
LOCAL_EXPORT_C_INCLUDES := ../prebuild/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libuuid
LOCAL_SRC_FILES := ../prebuild/libuuid.a
LOCAL_EXPORT_C_INCLUDES := ../prebuild/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libz
LOCAL_SRC_FILES := ../prebuild/libz.a
LOCAL_EXPORT_C_INCLUDES := ../prebuild/include
include $(PREBUILT_STATIC_LIBRARY)

# wrapper info
include $(CLEAR_VARS)
LOCAL_C_INCLUDES += ../prebuild/include
LOCAL_MODULE    := idxsearch
LOCAL_SRC_FILES := idxsearch.cpp
LOCAL_STATIC_LIBRARIES := libxapian libz libbrasscheck libchertcheck libflintcheck libuuid libgetopt
include $(BUILD_SHARED_LIBRARY)
