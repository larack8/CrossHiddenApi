
#include "CrossHiddenApi.h"
#include <android/log.h>
#include <vector>
#include <string>

#define PrintLog(...)  ((void)__android_log_print(ANDROID_LOG_INFO, "CrossHiddenApi-Native", __VA_ARGS__))

template<typename T>

int findOffset(void *start, int regionStart, int regionEnd, T value) {

    if (NULL == start || regionEnd <= 0 || regionStart < 0) {
        return -1;
    }

    char *c_start = (char *) start;

    for (int i = regionStart; i < regionEnd; i += 4) {
        T *current_value = (T *) (c_start + i);
        if (value == *current_value) {
            return i;
        }
    }
    return -2;
}

int closeCross(JNIEnv *env, jint targetSdk) {
    return crossProlicy(env, targetSdk, EnforcementPolicy::kDarkGreyAndBlackList);
}

int openCross(JNIEnv *env, jint targetSdk) {
    return crossProlicy(env, targetSdk, EnforcementPolicy::kNoChecks);
}

int crossProlicy(JNIEnv *env, jint targetSdk, EnforcementPolicy policy) {

    PrintLog("try to cross policy, jniEnv: %d, targetSdk: %lu, policy: %d", env, targetSdk, policy);

    // TODO 1.get jvm from jni env
    JavaVM *javaVM;
    env->GetJavaVM(&javaVM);

    JvmRuntime *jvmRuntime = (JvmRuntime *) javaVM;
    void *runtime = jvmRuntime->runtime;
    PrintLog("runtime: %p, jvmRuntime: %p", runtime, jvmRuntime);

    // TODO 2.get jvm sdk offset
    const int MAX = 2000;
    int jvmOffset = findOffset(runtime, 0, MAX, (size_t) jvmRuntime);
    PrintLog("jvmOffset: %d", jvmOffset);

    if (jvmOffset < 0) {
        return -1;
    }

    // TODO 3.get target sdk offset
    int targetSdkOffset = findOffset(runtime, jvmOffset, MAX, targetSdk);
    PrintLog("target sdk version offset is %d", targetSdkOffset);

    if (targetSdkOffset < 0) {
        return -2;
    }

    // TODO 4.get runtime info
    CrossRuntime *crossRuntime = (CrossRuntime *) ((char *) runtime + targetSdkOffset);
    PrintLog("Before cross hidden api, policy is %d", crossRuntime->hidden_api_policy_);

    // TODO 5.cross hidden api by modify policy
    crossRuntime->hidden_api_policy_ = policy;
    PrintLog("After cross hidden api, policy is %d", crossRuntime->hidden_api_policy_);
    return 0;
}

