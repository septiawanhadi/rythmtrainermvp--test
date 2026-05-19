#include <jni.h>
#include <android/log.h>
#include "RhythmEngine.h"

#define LOG_TAG "jni_bridge"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

// Singleton engine instance
static RhythmEngine gEngine;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_example_rhythmtrainermvp_RhythmBridge_nativeInit(
    JNIEnv* env, jobject /* this */,
    jint sampleRate, jint framesPerBurst) {
    LOGD("nativeInit(%d, %d)", sampleRate, framesPerBurst);
    return gEngine.init(sampleRate, framesPerBurst) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_example_rhythmtrainermvp_RhythmBridge_nativeStartCountIn(
    JNIEnv* env, jobject /* this */) {
    LOGD("nativeStartCountIn()");
    gEngine.startCountIn();
}

JNIEXPORT void JNICALL
Java_com_example_rhythmtrainermvp_RhythmBridge_nativeStartPlayback(
    JNIEnv* env, jobject /* this */) {
    LOGD("nativeStartPlayback()");
    gEngine.startPlayback();
}

JNIEXPORT void JNICALL
Java_com_example_rhythmtrainermvp_RhythmBridge_nativeStop(
    JNIEnv* env, jobject /* this */) {
    LOGD("nativeStop()");
    gEngine.stop();
}

JNIEXPORT void JNICALL
Java_com_example_rhythmtrainermvp_RhythmBridge_nativeDestroy(
    JNIEnv* env, jobject /* this */) {
    LOGD("nativeDestroy()");
    gEngine.destroy();
}

JNIEXPORT jlong JNICALL
Java_com_example_rhythmtrainermvp_RhythmBridge_nativeGetCurrentTimestampMs(
    JNIEnv* env, jobject /* this */) {
    return gEngine.getCurrentTimestampMs();
}

} // extern "C"
