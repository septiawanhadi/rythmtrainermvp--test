#include "audio_engine.h"
#include <android/log.h>
#include <jni.h>
#include <cmath>

#define TAG "AudioEngine"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

AudioEngine::AudioEngine() {
}

AudioEngine::~AudioEngine() {
    stop();
}

void AudioEngine::start() {
    oboe::AudioStreamBuilder builder;
    builder.setDirection(oboe::Direction::Output);
    builder.setPerformanceMode(oboe::PerformanceMode::LowLatency);
    builder.setSharingMode(oboe::SharingMode::Exclusive);
    builder.setFormat(oboe::AudioFormat::Float);
    builder.setChannelCount(1); // Mono
    builder.setDataCallback(this);

    oboe::Result result = builder.openStream(mStream);
    if (result != oboe::Result::OK) {
        LOGE("Failed to open stream. Error: %s", oboe::convertToText(result));
        return;
    }

    mSampleRate = mStream->getSampleRate();
    calculateFramesPerBeat();
    
    mIsPlaying = true;
    mFrameCounter = 0;
    
    result = mStream->requestStart();
    if (result != oboe::Result::OK) {
        LOGE("Failed to start stream. Error: %s", oboe::convertToText(result));
        mStream->close();
        mStream.reset();
    } else {
        LOGD("Audio stream started successfully");
    }
}

void AudioEngine::stop() {
    mIsPlaying = false;
    if (mStream) {
        mStream->requestStop();
        mStream->close();
        mStream.reset();
        LOGD("Audio stream stopped");
    }
}

void AudioEngine::calculateFramesPerBeat() {
    // 80 BPM
    float bpm = 80.0f;
    float beatsPerSecond = bpm / 60.0f;
    float secondsPerBeat = 1.0f / beatsPerSecond;
    mFramesPerBeat = static_cast<int32_t>(mSampleRate * secondsPerBeat);
}

oboe::DataCallbackResult
AudioEngine::onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) {
    if (!mIsPlaying) {
        return oboe::DataCallbackResult::Stop;
    }

    auto *outputData = static_cast<float *>(audioData);
    
    for (int i = 0; i < numFrames; ++i) {
        // Tembak tick audio singkat setiap ketukan (beat)
        if (mFrameCounter < 500) { 
            // Bunyi beep sederhana (1000Hz)
            outputData[i] = 0.5f * sinf(2.0f * M_PI * 1000.0f * (mFrameCounter / (float)mSampleRate));
        } else {
            outputData[i] = 0.0f;
        }

        mFrameCounter++;
        if (mFrameCounter >= mFramesPerBeat) {
            mFrameCounter = 0;
        }
    }

    return oboe::DataCallbackResult::Continue;
}

// --- JNI Bridge ---
extern "C" {

JNIEXPORT jlong JNICALL
Java_com_example_rhythmtrainermvp_audio_AudioController_nativeStartEngine(JNIEnv *env, jobject thiz) {
    auto *engine = new AudioEngine();
    engine->start();
    return reinterpret_cast<jlong>(engine);
}

JNIEXPORT void JNICALL
Java_com_example_rhythmtrainermvp_audio_AudioController_nativeStopEngine(JNIEnv *env, jobject thiz, jlong engine_handle) {
    if (engine_handle != 0) {
        auto *engine = reinterpret_cast<AudioEngine *>(engine_handle);
        engine->stop();
        delete engine;
    }
}

}
