#ifndef RHYTHMTRAINERMVP_AUDIO_ENGINE_H
#define RHYTHMTRAINERMVP_AUDIO_ENGINE_H

#include <oboe/Oboe.h>
#include <atomic>

class AudioEngine : public oboe::AudioStreamDataCallback {
public:
    AudioEngine();
    ~AudioEngine();

    void start();
    void stop();

    oboe::DataCallbackResult
    onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) override;

private:
    std::shared_ptr<oboe::AudioStream> mStream;
    std::atomic<bool> mIsPlaying{false};
    
    // Metronome parameters: 80 BPM, 4/4 time
    int32_t mSampleRate = 48000;
    int32_t mFramesPerBeat = 0;
    int32_t mFrameCounter = 0;
    
    void calculateFramesPerBeat();
};

#endif //RHYTHMTRAINERMVP_AUDIO_ENGINE_H
