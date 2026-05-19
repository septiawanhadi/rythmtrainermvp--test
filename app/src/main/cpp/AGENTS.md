# C++ NDK Layer — AGENTS.md

**Location:** `app/src/main/cpp/`
**Stack:** C++17 · CMake 3.22.1 · Oboe 1.9.0 · JNI · NDK

## OVERVIEW

Native audio engine for the rhythm trainer. Uses Oboe for low-latency audio output, generates metronome ticks via `AudioStreamDataCallback`, exposes a JNI bridge for Kotlin ↔ C++ communication. Builds as shared library `librhythmengine.so`.

## STRUCTURE

```
cpp/
├── CMakeLists.txt             # CMake — Oboe Prefab, shared lib
├── RhythmEngine.h             # AudioStreamDataCallback subclass
├── RhythmEngine.cpp           # Audio engine lifecycle + callback
├── MetronomePlayer.h          # WAV loading + sample buffer
├── MetronomePlayer.cpp        # Click sound playback
└── jni_bridge.cpp             # extern "C" JNI → engine calls
```

## WHERE TO LOOK

| Task | Location |
|------|----------|
| Audio callback | `RhythmEngine.h` → `onAudioReady()` |
| Metronome click | `MetronomePlayer.h/.cpp` — WAV load + play |
| JNI entry points | `jni_bridge.cpp` — `Java_com_example_rhythmtrainermvp_RhythmBridge_*` |
| Build/linkage | `CMakeLists.txt` — Oboe via `find_package`, `target_link_libraries` |

## CONVENTIONS

### Build

| Setting | Value | Location |
|---------|-------|----------|
| CMake minimum | 3.22.1 | `CMakeLists.txt:1` |
| C++ standard | C++17 | `build.gradle.kts` → `cppFlags("-std=c++17")` |
| STL | `c++_shared` | `build.gradle.kts` → `arguments("-DANDROID_STL=c++_shared")` |
| Oboe linkage | Prefab via `find_package(oboe REQUIRED CONFIG)` | `CMakeLists.txt:5` |
| Linked libs | `oboe::oboe android log` | `CMakeLists.txt:14-16` |
| Shared lib name | `rhythmengine` | `CMakeLists.txt:7` |
| Optimization | `-O2` | `build.gradle.kts` → `cppFlags` |

### JNI Bridge (`jni_bridge.cpp`)

- All JNI functions in `extern "C"` block
- Function naming: `Java_com_example_rhythmtrainermvp_RhythmBridge_nativeXxx`
- Singleton engine: `static unique_ptr<RhythmEngine> gEngine`
- Debug logging: `LOGD(...)` via `#include <android/log.h>`
- JNI calls manage engine lifecycle: `nativeInit` → creates, `nativeDestroy` → tears down

### Engine Pattern (`RhythmEngine`)

- Extends `oboe::AudioStreamDataCallback`
- Constructor: `RhythmEngine(int32_t sampleRate, int32_t framesPerBurst)`
- Owns `MetronomePlayer` for click audio
- `onAudioReady()` — called by Oboe on high-priority audio thread (must not block)
- `start()`, `stop()` control playback state

### MetronomePlayer

- Loads `.wav` file from APK assets via `AAssetManager`
- Stores decoded PCM samples in internal buffer
- `getNextSamples(float* buffer, int32_t numFrames)` — fills output buffer
- Placeholder stubs: WAV loading not yet implemented

## ANTI-PATTERNS

| ❌ | Fix |
|---|-----|
| C++ standard in `cppFlags` (Gradle) not CMake | Add `target_compile_features(rhythmengine PRIVATE cxx_std_17)` to `CMakeLists.txt` |
| No guard on `nativeInit` re-call | Check `gEngine` before creating — prevent silent overwrite |
| `LOGD` in audio callback path | `LOGD` is slow — gate behind `#ifdef DEBUG` or remove from hot path |
| `using namespace std` | Prefer explicit `std::` qualifiers in headers |

## NOTES

- **ODR safety**: `.h` files are straightforward — no inline function definitions requiring `inline` keyword
- **ABI uniformity**: Same code for all three ABIs (`arm64-v8a`, `armeabi-v7a`, `x86_64`)
- **No audio assets**: `metronome_click.wav` not yet present in `res/raw/` — MetronomePlayer is a stub
- **No PlatformConfig**: `RhythmEngine` uses Oboe defaults — may need tuning for latency vs battery
- **CMake rebuild**: Full rebuild on `CMakeLists.txt` change — incremental on `.cpp` changes
- **NDK version**: Not explicitly pinned — inherits from installed NDK
