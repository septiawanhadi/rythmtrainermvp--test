#include "RhythmEngine.h"
#include <android/log.h>
#include <chrono>

#define LOG_TAG "RhythmEngine"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

RhythmEngine::RhythmEngine() {}

RhythmEngine::~RhythmEngine() {
    stop();
}

bool RhythmEngine::init(int32_t sampleRate, int32_t framesPerBurst) {
    mSampleRate = sampleRate;
    mFramesPerBeat = static_cast<int32_t>(mSampleRate * kSecsPerBeat);

    oboe::AudioStreamBuilder builder;
    builder.setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setSharingMode(oboe::SharingMode::Exclusive)
            ->setFormat(oboe::AudioFormat::Float)
            ->setChannelCount(oboe::ChannelCount::Mono)
            ->setSampleRate(mSampleRate)
            ->setFramesPerDataCallback(framesPerBurst)
            ->setDataCallback(this);

    oboe::Result result = builder.openStream(mAudioStream);
    if (result != oboe::Result::OK) {
        LOGE("Error opening stream: %s", oboe::convertToText(result));
        return false;
    }

    return true;
}

void RhythmEngine::setSampleData(const float* data, int32_t numFrames) {
    mMetronomePlayer.setSampleData(data, numFrames);
}

void RhythmEngine::setJniInfo(JavaVM* jvm, jobject thiz) {
    mJvm = jvm;
    JNIEnv* env;
    if (mJvm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) return;
    mBridgeObj = env->NewGlobalRef(thiz);
    jclass clazz = env.FindClass("com/example/rhythmtrainermvp/RhythmBridge");
    onBeatTickId = env->GetStaticMethodID(clazz, "onBeatTick", "(IJ)V");
    onPlaybackCompleteId = env->GetStaticMethodID(clazz, "onPlaybackComplete", "()V");
}

void RhythmEngine::startCountIn() {
    mIsCountIn = true;
    mIsPlaying = false;
    mCurrentBeat.store(-1);
    mFrameCounter = 0;
    mMetronomePlayer.reset();

    if (mAudioStream) {
        mAudioStream->requestStart();
    }
}

void RhythmEngine::startPlayback() {
    mIsCountIn = false;
    mIsPlaying = true;
    mCurrentBeat.store(-1);
    mFrameCounter = 0;
    mMetronomePlayer.reset();

    if (mAudioStream) {
        mAudioStream->requestStart();
    }
}

void RhythmEngine::stop() {
    mIsPlaying = false;
    mIsCountIn = false;
    if (mAudioStream) {
        mAudioStream->requestStop();
    }
}

void RhythmEngine::destroy() {
    stop();
    if (mAudioStream) {
        mAudioStream->close();
        mAudioStream.reset();
    }
    if (mJvm && mBridgeObj) {
        JNIEnv* env;
        if (mJvm->GetEnv((void**)&env, JNI_VERSION_1_6) == JNI_OK) {
            env->DeleteGlobalRef(mBridgeObj);
        }
    }
}

int64_t RhythmEngine::getCurrentTimestampMs() {
    auto now = std::chrono::steady_clock::now();
    return std::chrono::duration_cast<std::chrono::milliseconds>(now.time_since_epoch()).count();
}

oboe::DataCallbackResult RhythmEngine::onAudioReady(
        oboe::AudioStream *audioStream,
        void *audioData,
        int32_t numFrames) {

    auto *outputData = static_cast<float *>(audioData);
    for (int i = 0; i < numFrames; ++i) outputData[i] = 0.0f;

    if (!mIsPlaying && !mIsCountIn) return oboe::DataCallbackResult::Continue;

    for (int i = 0; i < numFrames; ++i) {
        if (mFrameCounter % mFramesPerBeat == 0) {
            int32_t beat = mCurrentBeat.fetch_add(1) + 1;
            mMetronomePlayer.trigger();

            // Notify Kotlin
            fireJniBeatCallback(beat, getCurrentTimestampMs());

            if (mIsCountIn && beat >= 3) {
                // Count-in ends after 4 beats (0,1,2,3)
                // Kotlin will call startPlayback()
            }
            if (mIsPlaying && beat >= 15) {
                // Playback ends after 16 beats (0..15)
                mIsPlaying = false;
                fireJniPlaybackComplete();
            }
        }
        mFrameCounter++;
    }

    mMetronomePlayer.renderAudio(outputData, numFrames);

    return oboe::DataCallbackResult::Continue;
}

void RhythmEngine::fireJniBeatCallback(int beatIndex, int64_t timestampMs) {
    if (!mJvm || !mBridgeObj || !onBeatTickId) return;
    JNIEnv* env;
    mJvm->AttachCurrentThread(&env, nullptr);
    env->CallStaticVoidMethod(env->FindClass("com/example/rhythmtrainermvp/RhythmBridge"), onBeatTickId, beatIndex, timestampMs);
}

void RhythmEngine::fireJniPlaybackComplete() {
    if (!mJvm || !mBridgeObj || !onPlaybackCompleteId) return;
    JNIEnv* env;
    mJvm->AttachCurrentThread(&env, nullptr);
    env->CallStaticVoidMethod(env->FindClass("com/example/rhythmtrainermvp/RhythmBridge"), onPlaybackCompleteId);
}
