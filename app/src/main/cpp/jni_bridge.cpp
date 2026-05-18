#include <jni.h>
#include <android/log.h>
#include "RhythmEngine.h"

#define LOG_TAG "RhythmJNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

static RhythmEngine *engine = nullptr;
static JavaVM *gJvm = nullptr;

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    gJvm = vm;
    return JNI_VERSION_1_6;
}

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_example_rhythmtrainermvp_RhythmBridge_nativeInit(JNIEnv *env, jobject thiz, jint sample_rate, jint frames_per_burst) {
    if (engine == nullptr) {
        engine = new RhythmEngine();
    }

    bool result = engine->init(sample_rate, frames_per_burst);
    if (result) {
        engine->setJniInfo(gJvm, thiz);
    }
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_example_rhythmtrainermvp_RhythmBridge_nativeStartCountIn(JNIEnv *env, jobject thiz) {
    if (engine) engine->startCountIn();
}

JNIEXPORT void JNICALL
Java_com_example_rhythmtrainermvp_RhythmBridge_nativeStartPlayback(JNIEnv *env, jobject thiz) {
    if (engine) engine->startPlayback();
}

JNIEXPORT void JNICALL
Java_com_example_rhythmtrainermvp_RhythmBridge_nativeStop(JNIEnv *env, jobject thiz) {
    if (engine) engine->stop();
}

JNIEXPORT void JNICALL
Java_com_example_rhythmtrainermvp_RhythmBridge_nativeDestroy(JNIEnv *env, jobject thiz) {
    if (engine) {
        engine->destroy();
        delete engine;
        engine = nullptr;
    }
}

JNIEXPORT jlong JNICALL
Java_com_example_rhythmtrainermvp_RhythmBridge_nativeGetCurrentTimestampMs(JNIEnv *env, jobject thiz) {
    if (engine) return engine->getCurrentTimestampMs();
    return 0;
}

}
