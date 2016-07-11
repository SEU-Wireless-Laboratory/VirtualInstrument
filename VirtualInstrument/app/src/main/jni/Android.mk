LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#OPENCV_LIB_TYPE:=SHARED
#OPENCV_CAMERA_MODULES := off
#OPENCV_INSTALL_MODULES := off
include C:\OpenCV-android-sdk\sdk\native\jni\OpenCV.mk

LOCAL_SRC_FILES  := DetectionBasedTracker_jni.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl
LOCAL_MODULE     := ndk_jni_utils

include $(BUILD_SHARED_LIBRARY)
