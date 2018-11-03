#include <jni.h>
#include <string>
#include "CrossHiddenApi.h"

extern "C"
JNIEXPORT jint JNICALL
Java_com_larack_crosshiddenapi_CrossUtils_openCrossHiddenApiNative(JNIEnv *env, jclass type,
                                                                    jint targetSdkVersion) {

    return openCross(env, targetSdkVersion);

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_larack_crosshiddenapi_CrossUtils_closeCrossHiddenApiNative(JNIEnv *env, jclass type,
                                                                     jint targetSdkVersion) {

    return closeCross(env, targetSdkVersion);

}