#ifndef METRONOME_PLAYER_H
#define METRONOME_PLAYER_H

#include <vector>
#include <cstdint>

class MetronomePlayer {
public:
    MetronomePlayer() = default;
    ~MetronomePlayer() = default;

    bool loadClickSample(const char* wavPath);
    std::vector<float>& getSampleData() { return mSampleData; }
    int32_t getSampleRate() const { return mSampleRate; }

private:
    std::vector<float> mSampleData;
    int32_t mSampleRate = 0;
};

#endif // METRONOME_PLAYER_H
