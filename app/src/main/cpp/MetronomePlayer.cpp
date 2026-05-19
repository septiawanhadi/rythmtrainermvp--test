#include "MetronomePlayer.h"
#include <android/log.h>

#define LOG_TAG "MetronomePlayer"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

bool MetronomePlayer::loadClickSample(const char* wavPath) {
    LOGD("MetronomePlayer::loadClickSample(%s) — STUB", wavPath);
    // Phase 2: Load WAV file from assets via AAssetManager
    // Phase 2: Parse WAV header, extract PCM float samples
    return false; // stub: no WAV loaded yet
}
