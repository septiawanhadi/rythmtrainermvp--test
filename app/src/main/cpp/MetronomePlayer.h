#ifndef RHYTHMTRAINER_METRONOMEPLAYER_H
#define RHYTHMTRAINER_METRONOMEPLAYER_H

#include <vector>
#include <cstdint>
#include <atomic>

class MetronomePlayer {
public:
    void setSampleData(const float* data, int32_t numFrames);
    void trigger();
    void renderAudio(float* targetData, int32_t numFrames);
    void reset();

private:
    std::vector<float> mSampleData;
    std::atomic<int32_t> mReadIndex{-1}; // -1 means not playing
};

#endif //RHYTHMTRAINER_METRONOMEPLAYER_H
