# Rhythm Trainer MVP — AGENTS.md

**Generated:** 2026-05-19
**Stack:** Android · Kotlin 2.2.10 · Jetpack Compose · C++17 NDK · Oboe 1.9.0

## OVERVIEW

Rhythm trainer mobile app: user taps to displayed quarter notes in sync with an Oboe-driven audio metronome. Kotlin/Compose UI with Canvas-based notation rendering, C++17 audio engine linked via JNI.

## STRUCTURE

```
RhythmTrainer/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── cpp/                         # C++ NDK (see cpp/AGENTS.md)
│       │   ├── CMakeLists.txt
│       │   ├── RhythmEngine.h/.cpp      # AudioStreamDataCallback
│       │   ├── MetronomePlayer.h/.cpp   # WAV playback
│       │   └── jni_bridge.cpp           # JNI ↔ C++ glue
│       ├── java/com/example/rhythmtrainermvp/
│       │   ├── MainActivity.kt          # Single entry point
│       │   ├── RhythmBridge.kt          # ⛔ Sole JNI gateway
│       │   ├── data/                    # Models, generators, timing
│       │   ├── ui/                      # Compose UI (see ui/AGENTS.md)
│       │   └── viewmodel/
│       └── res/                         # Compose-first — no layouts/
├── build.gradle.kts                     # AGP 9.2.1 · Kotlin 2.2.10
├── settings.gradle.kts                  # Plugin repos + foojay resolver
├── gradle.properties                    # JVM + SDK flags
├── design.md                            # UI spec (Compose Canvas dark theme)
└── PRD_RhythmTrainer_MVP_Level1.md      # Full product requirements

✅ Standard Android project layout — no non-standard placements.
```

## WHERE TO LOOK

| Task | Location | Notes |
|------|----------|-------|
| Build config | `app/build.gradle.kts` | SDKs, NDK, Prefab, Oboe, STL |
| Plugin/repo config | `settings.gradle.kts` | `dependencyResolutionManagement` |
| JVM/SDK flags | `gradle.properties` | `org.gradle.java.home` pins JDK 21 |
| C++ / NDK | `app/src/main/cpp/` | Also see `cpp/AGENTS.md` |
| JNI bridge | `RhythmBridge.kt` ↔ `jni_bridge.cpp` | ⛔ All JNI through this object |
| State management | `viewmodel/GameViewModel.kt` | Single source of truth |
| Data models | `data/NotePattern.kt` `data/SessionScore.kt` | Note events, scoring |
| Pattern generation | `data/PatternGenerator.kt` | ⛔ Must produce exactly 16 events |
| Timing constants | `data/TimingConstants.kt` | BPM=80, windows, durations |
| UI screens | `ui/screens/` | See `ui/AGENTS.md` |
| UI components | `ui/components/` | See `ui/AGENTS.md` |
| Theme | `ui/theme/` | See `ui/AGENTS.md` |
| Haptics | `ui/HapticController.kt` | Vibrator API 26+/31+ |
| Planning docs | `design.md` · `PRD_*.md` | Authoritative specs |

## CONVENTIONS

### State Management
- `MutableStateFlow` private (`_foo`), `StateFlow` public via `asStateFlow()`
- All reactive state in `GameViewModel` — nowhere else
- No `LiveData` — project is Flow-only

### JNI Bridge
- **⛔ All JNI calls through `RhythmBridge` object** — never call `System.loadLibrary` elsewhere
- JNI function names follow Kotlin package path: `Java_com_example_rhythmtrainermvp_RhythmBridge_nativeXxx`
- `@Suppress("unused")` on `RhythmBridge` silences warnings for `external` methods not yet called (Phases 5–7)

### Theme (Compose)
- **⛔ Never hardcode hex colors outside `Color.kt`** — use `RhythmColors.*`
- Typography: `Inter` font family (placeholder: system sans-serif)
- Dimensions: all spacing/scaling in `Dimensions.kt`

### Naming
- Screen composables: `*Screen.kt`
- Overlays: `*Overlay.kt`
- ViewModels: `*ViewModel.kt`
- Theme objects: `Rhythm*` prefix

### Build
- `prefab = true` required for Oboe Prefab linkage
- C++ STL: `c++_shared` (Oboe requirement — NOT static)
- ABI filters: `arm64-v8a`, `armeabi-v7a`, `x86_64`
- minSdk 26 — no `coreLibraryDesugaring` needed
- Compose via Kotlin 2.x `plugin.compose` — no deprecated `composeOptions`

### Resources
- No `.txt` / non-standard files in `res/` subfolders (AAPT2 rejects)
- All resource filenames lowercase: `a-z`, `0-9`, `_` only
- App icon: `@drawable/logo_client` (no `mipmap-*`)
- `res/font/` and `res/raw/` are empty — ready for `.ttf` font files and `.wav` audio assets

## ANTI-PATTERNS

| ❌ | Fix |
|---|-----|
| Hardcoded hex in Composables | Use `RhythmColors.*` |
| JNI anywhere but `RhythmBridge` | ⛔ Forbidden |
| `PatternGenerator` ≠ 16 events | ⛔ Spec — app logic depends on 0–15 indices |
| `.txt` in `res/raw/` or `res/font/` | AAPT2 build break |
| `LiveData` usage | Flow-only project |
| `cppFlags("-std=c++17")` for C++ standard | Prefer `target_compile_features(rhythmengine PRIVATE cxx_std_17)` in CMakeLists.txt |

## SETUP FOR CONTRIBUTORS

### Required
- **Android Studio** (Ladybug 2024.2+)
- **JDK 21** (Eclipse Temurin — `org.gradle.java.home` in `gradle.properties`)
- **Android SDK 35** + NDK (SDK Manager → SDK Tools → NDK)
- **CMake 3.22.1+**

### First Sync
1. File → Project Structure → SDK Location → set Android SDK path
2. Sync Gradle — `prefab = true` auto-resolves Oboe
3. Build: `:app:assembleDebug`

### NDK & CMake Environment

| Setting | Value | Location |
|---------|-------|----------|
| C++ Standard | C++17 | `app/build.gradle.kts` → `cppFlags` |
| STL | `c++_shared` | `app/build.gradle.kts` → `arguments` |
| CMake min | 3.22.1 | `CMakeLists.txt` |
| Oboe | 1.9.0 Prefab | `app/build.gradle.kts` + `CMakeLists.txt` |
| ABI Filters | arm64-v8a, armeabi-v7a, x86_64 | `app/build.gradle.kts` |
| minSdk | 26 | `app/build.gradle.kts` |
| targetSdk / compileSdk | 35 | `app/build.gradle.kts` |

### Key Files for AI Agents

| File | Purpose |
|------|---------|
| `app/build.gradle.kts` | Dependency + NDK build logic |
| `app/src/main/cpp/CMakeLists.txt` | C++ library + Oboe linkage |
| `app/src/main/AndroidManifest.xml` | Permissions (VIBRATE), activity config |
| `gradle.properties` | JVM flags, SDK flags, JDK path |
| `settings.gradle.kts` | Plugin management, repo config |
| `.idea/dictionaries/project.xml` | Shared project dictionary — silences spelling warnings for all contributors |

## COMMANDS

```bash
# Debug APK (all ABIs)
./gradlew :app:assembleDebug

# Single ABI (faster dev)
./gradlew :app:assembleDebug -Pandroid.injected.build.abi=arm64-v8a
```

## NOTES

- **No gradlew in repo** — Android Studio generates on open. Use `gradle wrapper` if needed.
- **Phase-based dev**: TODO comments reference PRD Phases 3–7; all are forward-looking stubs.
- **No lint/ktlint/detekt** config — Android Studio defaults only.
- **`org.gradle.java.home`** pins JDK 21 in `gradle.properties` — update if path changes.
- **app icon** references `@drawable/logo_client` — no `mipmap-*` variants exist.
- **daemon toolchain migration** may fail with `--jvm-vendor` error on some JDKs — non-blocking; dismiss.
