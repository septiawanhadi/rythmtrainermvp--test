#include "RhythmEngine.h"
#include <android/log.h>

#define LOG_TAG "RhythmEngine"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

RhythmEngine::RhythmEngine() = default;
RhythmEngine::~RhythmEngine() = default;

bool RhythmEngine::init(int32_t sampleRate, int32_t framesPerBurst) {
    LOGD("RhythmEngine::init(sampleRate=%d, framesPerBurst=%d) — STUB", sampleRate, framesPerBurst);
    // Phase 2: Create Oboe stream with LowLatency/Exclusive
    // Phase 2: Load metronome_click.wav into mClickSampleData
    mSampleRate = sampleRate;
    return true; // stub: pretend success
}

void RhythmEngine::startCountIn() {
    LOGD("RhythmEngine::startCountIn() — STUB");
    // Phase 2: Play 4 metronome beats, fire JNI callbacks
}

void RhythmEngine::startPlayback() {
    LOGD("RhythmEngine::startPlayback() — STUB");
    // Phase 2: Play 16-beat loop, fire JNI callbacks
}

void RhythmEngine::stop() {
    LOGD("RhythmEngine::stop() — STUB");
    // Close audio stream
}

void RhythmEngine::destroy() {
    LOGD("RhythmEngine::destroy() — STUB");
    // Release all resources
}

int64_t RhythmEngine::getCurrentTimestampMs() {
    // Phase 2: Return engine clock derived from audio stream position
    return 0L;
}

oboe::DataCallbackResult RhythmEngine::onAudioReady(
    oboe::AudioStream* stream,
    void* audioData,
    int32_t numFrames) {
    // Phase 2: Fill audio buffer with metronome clicks
    // Phase 2: Fire beat callbacks at correct timing
    return oboe::DataCallbackResult::Continue;
}

void RhythmEngine::fireJniBeatCallback(int beatIndex, int64_t timestampMs) {
    // Phase 2: Call JNI callback to Kotlin via registered method
    LOGD("Beat tick: %d at %lld ms — STUB", beatIndex, timestampMs);
}
