#include "MetronomePlayer.h"
#include <cstring>

void MetronomePlayer::setSampleData(const float* data, int32_t numFrames) {
    mSampleData.assign(data, data + numFrames);
}

void MetronomePlayer::trigger() {
    mReadIndex.store(0);
}

void MetronomePlayer::renderAudio(float* targetData, int32_t numFrames) {
    int32_t index = mReadIndex.load();
    if (index == -1) return;

    for (int i = 0; i < numFrames; ++i) {
        if (index < mSampleData.size()) {
            targetData[i] += mSampleData[index++];
        } else {
            index = -1;
            break;
        }
    }
    mReadIndex.store(index);
}

void MetronomePlayer::reset() {
    mReadIndex.store(-1);
}
