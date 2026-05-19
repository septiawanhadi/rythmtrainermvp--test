#ifndef RHYTHM_ENGINE_H
#define RHYTHM_ENGINE_H

#include <oboe/Oboe.h>
#include <vector>
#include <memory>
#include <cstdint>

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

    oboe::DataCallbackResult onAudioReady(
        oboe::AudioStream* stream,
        void* audioData,
        int32_t numFrames) override;

private:
    std::shared_ptr<oboe::AudioStream> mAudioStream;
    std::vector<float> mClickSampleData;
    int32_t mSampleRate = 0;
    int64_t mStreamStartTimeNs = 0;
    int32_t mCurrentBeat = -1;
    bool mIsPlaying = false;
    bool mIsCountIn = false;

    void fireJniBeatCallback(int beatIndex, int64_t timestampMs);
};

#endif // RHYTHM_ENGINE_H
