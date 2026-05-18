#ifndef RHYTHMTRAINER_RHYTHMENGINE_H
#define RHYTHMTRAINER_RHYTHMENGINE_H

#include <oboe/Oboe.h>
#include <vector>
#include <atomic>
#include <jni.h>
#include "MetronomePlayer.h"

class RhythmEngine : public oboe::AudioStreamDataCallback {
public:
    RhythmEngine();
    ~RhythmEngine();

    bool init(int32_t sampleRate, int32_t framesPerBurst);
    void startCountIn();
    void startPlayback();
    void stop();
    void destroy();
    int64_t getCurrentTimestampMs();

    void setSampleData(const float* data, int32_t numFrames);
    void setJniInfo(JavaVM* jvm, jobject thiz);

    oboe::DataCallbackResult onAudioReady(
            oboe::AudioStream *audioStream,
            void *audioData,
            int32_t numFrames) override;

private:
    std::shared_ptr<oboe::AudioStream> mAudioStream;
    MetronomePlayer mMetronomePlayer;

    int32_t mSampleRate = 48000;
    std::atomic<bool> mIsPlaying{false};
    std::atomic<bool> mIsCountIn{false};

    std::atomic<int64_t> mPlaybackStartTimestamp{0};
    std::atomic<int32_t> mCurrentBeat{-1};

    const double kBpm = 80.0;
    const double kSecsPerBeat = 60.0 / kBpm;
    int32_t mFramesPerBeat = 0;
    int32_t mFrameCounter = 0;

    JavaVM* mJvm = nullptr;
    jobject mBridgeObj = nullptr;
    jmethodID onBeatTickId = nullptr;
    jmethodID onPlaybackCompleteId = nullptr;

    void fireJniBeatCallback(int beatIndex, int64_t timestampMs);
    void fireJniPlaybackComplete();
    int64_t getSystemTimeMs();
};

#endif //RHYTHMTRAINER_RHYTHMENGINE_H
