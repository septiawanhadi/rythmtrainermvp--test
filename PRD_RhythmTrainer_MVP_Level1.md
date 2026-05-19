# PRD — Rhythm Trainer MVP (Level 1)
**Version:** 1.0  
**Date:** May 17, 2026  
**Platform:** Android Native (minSdk 26 / Android 8.0)  
**Agent Workflow:** Build Phase-by-Phase → each phase must compile and run on physical device before next phase begins.

**Stack:**
- Language: Kotlin (UI layer) + C++ (audio engine layer)
- UI: Jetpack Compose + Canvas API
- Audio: Google Oboe Library (via NDK / CMake)
- Bridge: JNI (Java Native Interface)
- State: MVVM + StateFlow
- Persistence: ViewModel-scoped (in-memory only, MVP)
- Build: Android Studio (Iguana/Jellyfish), NDK, CMake

---

## Section 0: Agent Instructions

```
You are a Senior Android Native Engineer building a rhythm game app called
"Rhythm Trainer MVP Level 1" for Android (minSdk 26).

Your stack is Kotlin + Jetpack Compose (UI) and C++ via Google Oboe (audio engine).
The bridge between layers is JNI.

Follow this PRD section by section. NEVER skip phases.
NEVER generate placeholder logic — every component must be wired and functional.
ALWAYS use the design tokens defined in Section 1 before writing any UI code.
ALWAYS use Jetpack Compose Canvas for all music notation drawing — NEVER use PNG/SVG assets for notes, staff lines, or rests.
ALWAYS test audio timing on a physical Android device — NEVER use an emulator for audio latency tests.
NEVER use MP3 or OGG for audio assets — only .WAV uncompressed.
NEVER use Retrofit, REST, or any network call — this is a fully offline app.
NEVER add any login, auth, or user account feature.

After each phase, output:
"✅ Phase [N] complete — compiled and tested on physical device. Ready for review."

Wait for explicit approval before starting the next phase.

Reference repositories (study logic, DO NOT copy UI code verbatim):
- https://github.com/google/oboe/tree/main/samples/RhythmGame  ← primary audio/scoring reference
- https://github.com/ppy/osu  ← HitObject / HitWindows scoring logic (translate to Kotlin)
- https://github.com/android/ndk-samples/tree/main/audio-echo  ← JNI bridge reference
```

---

## Section 1: Design System (Brand & Tokens)

### 1.1 Color Tokens

```kotlin
// /ui/theme/Color.kt — SINGLE SOURCE OF TRUTH for all colors
// ⛔ RULE: Never hardcode hex values outside this file.

object RhythmColors {
    // Base
    val AppBackground   = Color(0xFF121212)   // Material Dark charcoal
    val TapZoneBg       = Color(0xFF121212)   // same as app bg, no visual border

    // Staff & Notation
    val StaffLine       = Color(0x4DFFFFFF)   // white at 30% opacity
    val NoteDefault     = Color(0xFFE0E0E0)   // off-white, upcoming note
    val NoteStem        = Color(0xFFE0E0E0)   // same as head

    // Playhead
    val Playhead        = Color(0xFF00E676)   // Neon Green — must glow
    val PlayheadGlow    = Color(0x4000E676)   // Neon Green at 25% for glow halo

    // Scoring states (applied to note head fill)
    val HitPerfect      = Color(0xFF00E676)   // Neon Green
    val HitGood         = Color(0xFFFFD600)   // Amber/Yellow
    val HitMiss         = Color(0xFFFF1744)   // Vibrant Red
    val FalseTap        = Color(0xFFFF1744)   // same as Miss

    // Text
    val TextPrimary     = Color(0xFFFFFFFF)
    val TextInstruction = Color(0xFFE0E0E0)
    val TextCountIn     = Color(0xFF00E676)   // count-in digits rendered in accent

    // Overlay
    val SummaryOverlay  = Color(0xCC121212)   // 80% opacity black for summary modal
    val SummaryCard     = Color(0xFF1E1E1E)
}
```

### 1.2 Typography

```kotlin
// /ui/theme/Type.kt
// Font: Inter (Google Fonts — use downloadable font in res/font/)
// Fallback: sans-serif system

val RhythmTypography = Typography(
    // "Tap Anywhere to Start" instruction
    displayLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = 0.5.sp,
        color = RhythmColors.TextInstruction
    ),
    // Count-In digits (1, 2, 3, 4)
    displayMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 72.sp,
        color = RhythmColors.TextCountIn
    ),
    // Scoring feedback popup text (Perfect / Good / Miss)
    headlineLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    // Summary modal labels
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = RhythmColors.TextPrimary
    ),
    // Retry button
    labelLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    )
)
```

### 1.3 Spacing & Layout Constants

```kotlin
// /ui/theme/Dimensions.kt
object RhythmDimensions {
    // Screen split
    val DisplayZoneHeightFraction = 0.40f   // top 40% = NotationCanvas
    val TapZoneHeightFraction     = 0.60f   // bottom 60% = Tap Zone

    // Canvas internals
    val StaffLineCount            = 5
    val StaffLineSpacingDp        = 10.dp   // space between each of the 5 lines
    val StaffTopPaddingFraction   = 0.15f   // % of DisplayZone height above top staff line
    val NoteHeadRadiusXDp         = 7.dp
    val NoteHeadRadiusYDp         = 5.dp
    val NoteStemHeightDp          = 35.dp
    val NoteStemWidthDp           = 2.dp
    val PlayheadWidthDp           = 3.dp
    val PlayheadFixedXFraction    = 0.20f   // playhead sits at 20% of canvas width (fixed)

    // Scoring feedback text appears at top-center of screen
    val FeedbackTextTopOffsetDp   = 24.dp

    // Summary modal
    val SummaryCardCornerDp       = 16.dp
    val SummaryCardPaddingDp      = 24.dp

    // Splash logo height
    val SplashLogoHeightDp        = 80.dp
}
```

### 1.4 Animation Constants

```kotlin
// /ui/theme/Animations.kt
object RhythmAnimations {
    // Note scroll: notes move LEFT at constant px/ms rate derived from BPM
    // Formula: pixelsPerMs = canvasWidth * (1 - PlayheadXFraction) / ((16 beats) * msPerBeat)
    // at 80 BPM: msPerBeat = 750ms, total duration = 16 * 750 = 12000ms

    // Feedback text: fade in immediately on tap, fade out after 400ms
    val FeedbackFadeInMs   = 0L
    val FeedbackVisibleMs  = 300L
    val FeedbackFadeOutMs  = 400L

    // Count-in digit: each digit shows for 650ms, then crossfade to next
    val CountInDigitMs     = 750L   // matches one beat at 80 BPM

    // Summary overlay: slide up from bottom over 300ms
    val SummarySlideMs     = 300L

    // Splash screen: minimum display 1500ms (while Oboe library loads)
    val SplashMinMs        = 1500L
}
```

---

## Section 2: Project Architecture

### 2.1 Module Structure

```
app/
├── src/
│   ├── main/
│   │   ├── cpp/
│   │   │   ├── CMakeLists.txt
│   │   │   ├── RhythmEngine.h
│   │   │   ├── RhythmEngine.cpp       ← Oboe audio engine + metronome
│   │   │   ├── MetronomePlayer.h
│   │   │   ├── MetronomePlayer.cpp    ← WAV loader + playback via Oboe
│   │   │   └── jni_bridge.cpp         ← JNI function declarations
│   │   ├── kotlin/
│   │   │   ├── MainActivity.kt
│   │   │   ├── RhythmBridge.kt        ← Kotlin object with external fun declarations
│   │   │   ├── data/
│   │   │   │   ├── NotePattern.kt     ← data class: NoteEvent list
│   │   │   │   └── PatternGenerator.kt ← generates random 4-bar pattern
│   │   │   ├── ui/
│   │   │   │   ├── theme/
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Type.kt
│   │   │   │   │   ├── Dimensions.kt
│   │   │   │   │   └── Animations.kt
│   │   │   │   ├── screens/
│   │   │   │   │   ├── SplashScreen.kt
│   │   │   │   │   ├── GameScreen.kt
│   │   │   │   │   └── SummaryOverlay.kt
│   │   │   │   └── components/
│   │   │   │       ├── NotationCanvas.kt  ← all Canvas drawing
│   │   │   │       ├── TapZone.kt
│   │   │   │       ├── CountInOverlay.kt
│   │   │   │       └── FeedbackText.kt
│   │   │   └── viewmodel/
│   │   │       └── GameViewModel.kt
│   │   └── res/
│   │       ├── raw/
│   │       │   └── metronome_click.wav   ← WAV, <100ms, woodblock/hi-hat
│   │       └── drawable/
│   │           ├── logo_developer.xml    ← vector drawable (co-brand)
│   │           └── logo_client.xml       ← vector drawable (co-brand)
├── build.gradle.kts
└── CMakeLists.txt (root level reference)
```

### 2.2 MVVM State Flow

```
[Oboe C++ Engine]
        │  JNI callback (every beat tick)
        ▼
[GameViewModel : ViewModel]
  - gameState: StateFlow<GameState>
  - playheadFraction: StateFlow<Float>       ← 0.0f to 1.0f across 16 beats
  - currentBeat: StateFlow<Int>              ← 0–15
  - noteResults: StateFlow<List<NoteResult>>
  - countInBeat: StateFlow<Int?>             ← 1,2,3,4 during count-in, null otherwise
  - showSummary: StateFlow<Boolean>
        │  collectAsState()
        ▼
[GameScreen Composable]
  ├── NotationCanvas (reads playheadFraction, noteResults)
  ├── FeedbackText (reads last NoteResult)
  ├── CountInOverlay (reads countInBeat)
  ├── TapZone (fires onTap → ViewModel.onUserTap())
  └── SummaryOverlay (reads showSummary + noteResults)
```

### 2.3 JNI Bridge Contract

```kotlin
// /viewmodel/RhythmBridge.kt
// ⛔ RULE: ALL JNI calls go through this object — never call System.loadLibrary elsewhere.

object RhythmBridge {
    init {
        System.loadLibrary("rhythmengine")
    }

    // Called from Kotlin → C++
    external fun nativeInit(sampleRate: Int, framesPerBurst: Int): Boolean
    external fun nativeStartCountIn()         // starts 4-beat count-in
    external fun nativeStartPlayback()        // starts 16-beat gameplay loop
    external fun nativeStop()
    external fun nativeDestroy()
    external fun nativeGetCurrentTimestampMs(): Long  // returns engine clock in ms

    // Called from C++ → Kotlin (registered via JNI callbacks)
    // These are called ON THE AUDIO THREAD — must post to Main thread before touching UI
    // C++ will call: onBeatTick(beatIndex: Int, timestampMs: Long)
    // C++ will call: onPlaybackComplete()
}
```

### 2.4 Oboe Configuration (CMakeLists.txt excerpt)

```cmake
cmake_minimum_required(VERSION 3.22.1)
project(rhythmengine)

# Oboe via Prefab (add to build.gradle: implementation("com.google.oboe:oboe:1.8.0"))
find_package(oboe REQUIRED CONFIG)

add_library(rhythmengine SHARED
    RhythmEngine.cpp
    MetronomePlayer.cpp
    jni_bridge.cpp
)

target_link_libraries(rhythmengine
    oboe::oboe
    android
    log
)
```

```kotlin
// build.gradle.kts (app) — key additions
android {
    defaultConfig {
        externalNativeBuild {
            cmake { cppFlags("-std=c++17", "-O2") }
        }
    }
    externalNativeBuild {
        cmake { path("src/main/cpp/CMakeLists.txt") }
    }
}

dependencies {
    implementation("com.google.oboe:oboe:1.8.0")  // Prefab
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
}
```

---

## Section 3: Data Schemas

### 3.1 NoteEvent

```kotlin
// /data/NotePattern.kt

enum class NoteType { QUARTER_NOTE, QUARTER_REST }

enum class NoteResult { PENDING, PERFECT, GOOD, MISS, FALSE_TAP }

data class NoteEvent(
    val index: Int,          // 0–15 (beat position in the 16-beat sequence)
    val type: NoteType,
    val timestampMs: Long,   // absolute engine time (ms) when this beat fires
                             // calculated at pattern init: index * 750L (at 80 BPM)
    var result: NoteResult = NoteResult.PENDING
)
```

### 3.2 GameState

```kotlin
enum class GameState {
    IDLE,        // app just opened, "Tap Anywhere to Start" visible
    COUNT_IN,    // 4-beat metronome count-in running, playhead not yet moving
    PLAYING,     // 16-beat gameplay, playhead moving
    SUMMARY      // game over, SummaryOverlay shown
}
```

### 3.3 SessionScore

```kotlin
data class SessionScore(
    val perfect: Int,
    val good: Int,
    val miss: Int,
    val falseTaps: Int,
    val totalNotes: Int        // count of QUARTER_NOTE events only (not rests)
) {
    val accuracyPercent: Float
        get() = if (totalNotes == 0) 0f
                else (perfect + good) / totalNotes.toFloat() * 100f
}
```

### 3.4 PatternGenerator

```kotlin
// /data/PatternGenerator.kt
// Generates a reproducible list of 16 NoteEvents for a 4-bar, 4/4, 80 BPM pattern.
// ⛔ RULE: Must always produce exactly 16 NoteEvents with indices 0–15.
// ⛔ RULE: Must always have at least 4 QUARTER_NOTE and at least 2 QUARTER_REST
//          per generation to guarantee meaningful gameplay and rest handling.

object PatternGenerator {
    private const val MS_PER_BEAT = 750L   // 80 BPM → 60000 / 80 = 750ms
    private const val TOTAL_BEATS = 16

    fun generate(seed: Long = System.currentTimeMillis()): List<NoteEvent> {
        val random = Random(seed)
        return (0 until TOTAL_BEATS).map { i ->
            NoteEvent(
                index = i,
                type = if (random.nextFloat() < 0.75f) NoteType.QUARTER_NOTE
                       else NoteType.QUARTER_REST,
                timestampMs = i * MS_PER_BEAT
            )
        }
    }
}
```

---

## Section 4: Screens & Components

### 4.1 SplashScreen

**Purpose:** Load the NDK library, display co-branding, then navigate to GameScreen.

**Layout:**
```
┌─────────────────────────────────────┐
│                                     │
│                                     │
│   [Logo Developer] | [Logo Client]  │  ← horizontal, center-aligned
│  "Developed in partnership with"    │  ← caption below logos, 12sp, TextMuted
│                                     │
│                                     │
└─────────────────────────────────────┘
```

**Behavior:**
- On launch, call `RhythmBridge.nativeInit(sampleRate, framesPerBurst)` inside `LaunchedEffect`.
- Get `sampleRate` from `AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE`.
- Get `framesPerBurst` from `AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER`.
- Minimum display time: `RhythmAnimations.SplashMinMs` (1500ms).
- Navigate to `GameScreen` only after BOTH library init AND minimum time have completed.
- If `nativeInit` returns `false`: show an error text "Audio engine failed to initialize" and a Retry button.

**Assets:**
- `R.drawable.logo_developer` (vector drawable)
- `R.drawable.logo_client` (vector drawable)
- Both logos rendered at `RhythmDimensions.SplashLogoHeightDp` (80dp) height, aspect-ratio preserved.
- Separator: a 1dp vertical line `Color(0x55FFFFFF)` between the two logos.

---

### 4.2 GameScreen

**Overall Layout:**

```kotlin
// GameScreen.kt structure
Box(modifier = Modifier.fillMaxSize().background(RhythmColors.AppBackground)) {

    Column(modifier = Modifier.fillMaxSize()) {

        // TOP 40% — Display Zone
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.40f)) {
            NotationCanvas(...)          // layer 0 (bottom): notes + staff + playhead
            FeedbackText(...)            // layer 1 (top): "Perfect" / "Good" / "Miss"
            CountInOverlay(...)          // layer 2 (top): count-in digits
        }

        // BOTTOM 60% — Tap Zone
        TapZone(
            modifier = Modifier.fillMaxWidth().weight(1f),
            onTap = { viewModel.onUserTap() }
        )
    }

    // IDLE overlay — "Tap Anywhere to Start"
    if (gameState == GameState.IDLE) {
        InstructionOverlay("Tap Anywhere to Start")
    }

    // SUMMARY overlay — slides up over entire screen
    if (gameState == GameState.SUMMARY) {
        SummaryOverlay(score = sessionScore, onRetry = { viewModel.onRetry() })
    }

    // Settings icon — top-right corner, always visible
    IconButton(
        onClick = { /* navigate to CreditsScreen */ },
        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
    ) {
        Icon(Icons.Default.Info, contentDescription = "Credits")
    }
}
```

**Z-Index rules:**
- `NotationCanvas` is always the bottom-most layer inside Display Zone.
- `FeedbackText` and `CountInOverlay` are always on top of `NotationCanvas`.
- `InstructionOverlay` covers the full screen but sits below `SummaryOverlay`.
- `SummaryOverlay` covers the full screen at topmost z-order.
- ⛔ RULE: No text element may be placed at coordinates that overlap with staff line rendering area unless it has a solid/gradient background scrim behind it.

---

### 4.3 NotationCanvas (Component)

**Purpose:** Renders the full music score: 5 staff lines, 16 NoteEvents (notes + rests), and the moving playhead.

**Rendering approach — Jetpack Compose Canvas:**

```kotlin
// NotationCanvas.kt
Canvas(modifier = Modifier.fillMaxSize()) {

    // 1. Draw 5 staff lines (static, drawn every frame)
    drawStaffLines()

    // 2. Draw all 16 NoteEvents at their X positions
    // X position of each note is STATIC (notes do NOT scroll)
    // Only the playhead moves
    noteEvents.forEach { note ->
        when (note.type) {
            NoteType.QUARTER_NOTE -> drawQuarterNote(note, staffConfig)
            NoteType.QUARTER_REST -> drawQuarterRest(note, staffConfig)
        }
    }

    // 3. Draw playhead at current X position derived from playheadFraction
    drawPlayhead(playheadFraction)
}
```

**Staff Layout:**
```
Canvas height = Display Zone height (40% of screen)
Staff top offset = staffTopPadding  (≈ 15% of canvas height)
Space between staff lines = staffSpacing (10dp converted to px)
Staff spans full canvas width
```

**Note Positioning (X-axis):**
```
// The 16 notes are spaced evenly across the canvas width.
// noteXFraction(index) = (index + 0.5) / 16.0
// noteX(index) = canvasWidth * noteXFraction(index)
// This places beat 0 at 3.125%, beat 15 at 96.875% of canvas width.
```

**Note Positioning (Y-axis):**
```
// All Quarter Notes sit on the 3rd staff line (middle line) for MVP.
// noteY = staffTopY + (2 * staffSpacing)  ← index 0 = top line, index 2 = middle
// Stem direction: stem goes UP (from note head to noteHead.y - stemHeight)
```

**Quarter Note Drawing:**
```kotlin
fun DrawScope.drawQuarterNote(note: NoteEvent, config: StaffConfig) {
    val color = note.result.toColor()  // resolved via extension
    val cx = config.noteX(note.index)
    val cy = config.noteY()
    // Oval head
    drawOval(
        color = color,
        topLeft = Offset(cx - noteHeadRx, cy - noteHeadRy),
        size = Size(noteHeadRx * 2, noteHeadRy * 2)
    )
    // Stem (upward)
    drawLine(
        color = color,
        start = Offset(cx + noteHeadRx, cy),
        end = Offset(cx + noteHeadRx, cy - stemHeight),
        strokeWidth = stemWidth
    )
}
```

**Quarter Rest Drawing:**
```kotlin
// Draw a simplified quarter rest symbol using Path.
// Shape: a zigzag / hook pattern approximating the standard rest glyph (𝄽).
// Centered at the same Y as a note, same X position.
// Color: always RhythmColors.NoteDefault (rests do not change color on false taps
//         — instead a red ❌ icon appears as an overlay above the rest position)
fun DrawScope.drawQuarterRest(note: NoteEvent, config: StaffConfig) { ... }
```

**Playhead Drawing:**
```kotlin
// The playhead is a vertical line at a fixed X position (20% from left edge).
// The NOTES move — wait, no: re-read constraint below.
//
// ⛔ ARCHITECTURAL DECISION:
// For MVP Level 1 with only 16 notes, ALL notes fit on screen simultaneously.
// Therefore: NOTES ARE STATIC, PLAYHEAD MOVES RIGHT from left to right.
// Playhead X = canvasWidth * playheadFraction
// playheadFraction starts at 0.0 (beat 0 note X) and ends at 1.0.
//
// Glow effect: draw the playhead line twice:
//   1st pass: width=8dp, color=PlayheadGlow (soft glow halo)
//   2nd pass: width=3dp, color=Playhead (solid neon line)

fun DrawScope.drawPlayhead(fraction: Float) {
    val x = size.width * fraction
    // Glow
    drawLine(PlayheadGlow, Offset(x, 0f), Offset(x, size.height), strokeWidth = 8.dp.toPx())
    // Sharp line
    drawLine(Playhead, Offset(x, 0f), Offset(x, size.height), strokeWidth = 3.dp.toPx())
}
```

**NoteResult → Color mapping:**
```kotlin
fun NoteResult.toColor(): Color = when (this) {
    NoteResult.PENDING    -> RhythmColors.NoteDefault
    NoteResult.PERFECT    -> RhythmColors.HitPerfect
    NoteResult.GOOD       -> RhythmColors.HitGood
    NoteResult.MISS       -> RhythmColors.HitMiss
    NoteResult.FALSE_TAP  -> RhythmColors.FalseTap
}
```

---

### 4.4 TapZone (Component)

```kotlin
// TapZone.kt
// A full-surface invisible tap catcher. No buttons, no labels.
Box(
    modifier = modifier
        .background(RhythmColors.AppBackground)
        .pointerInput(Unit) {
            detectTapGestures(onTap = { onTap() })
        }
)
// ⛔ RULE: No visible UI elements inside TapZone.
// Optionally: draw a very faint ripple animation on tap for visual confirmation.
```

---

### 4.5 FeedbackText (Component)

**Purpose:** Shows "Perfect!", "Good!", or "Miss!" centered at top of screen, briefly.

```kotlin
// FeedbackText.kt
// Position: top-center of the FULL screen (not just Display Zone)
// Top offset: FeedbackTextTopOffsetDp (24dp from top)
// Font: RhythmTypography.headlineLarge
// Has its own background scrim: rounded rectangle behind text for contrast
//
// Lifecycle: appears instantly on tap result, visible 300ms, fades out over 400ms.
// State: driven by lastFeedback: StateFlow<Pair<NoteResult, Long>?>
//        (NoteResult + timestamp so same result triggers recomposition)

// Color per result:
// Perfect → HitPerfect (green)
// Good    → HitGood (amber)
// Miss    → HitMiss (red)
// FalseTap → HitMiss (red) with text "False!"
```

---

### 4.6 CountInOverlay (Component)

```kotlin
// CountInOverlay.kt
// Displayed only during GameState.COUNT_IN.
// Shows digits 1, 2, 3, 4 — each for exactly 750ms (one beat at 80 BPM).
// Digit rendered in TextStyle displayMedium (72sp, neon green).
// Centered on screen over a semi-transparent scrim.
// After beat 4, overlay disappears and GameState transitions to PLAYING.
```

---

### 4.7 SummaryOverlay (Component)

```kotlin
// SummaryOverlay.kt
// Full-screen overlay sliding up from bottom over 300ms.
// Background: SummaryOverlay color (0xCC121212)
// Card: rounded RhythmColors.SummaryCard centered in screen

// Layout:
//   Title: "Results"            (headlineLarge, TextPrimary)
//   ─────────────────────────
//   ✅ Perfect:   [N]           (bodyLarge, HitPerfect color for value)
//   ✅ Good:      [N]           (bodyLarge, HitGood color for value)
//   ❌ Miss:      [N]           (bodyLarge, HitMiss color for value)
//   ❌ False Taps: [N]          (bodyLarge, HitMiss color for value)
//   ─────────────────────────
//   Accuracy:    [XX.X%]        (bodyLarge, bold, TextPrimary)
//   ─────────────────────────
//   [        RETRY         ]    ← full-width filled button, accent green

// Retry action: reset all state, regenerate new pattern, return to IDLE.
// ⛔ RULE: No "Next Level" or "Share" button in MVP.
```

---

### 4.8 CreditsScreen (Static Screen)

**Accessible via:** Info icon (top-right of GameScreen).

**Layout:**
```
Card-based list:
┌─────────────────────────────────┐
│  [Name]   [Role]                │
│  [Hyperlink to portfolio]       │
└─────────────────────────────────┘

Repeat per team member.
Back navigation: system back button.
```

```kotlin
data class CreditEntry(
    val name: String,
    val role: String,
    val portfolioUrl: String
)

// Hardcoded list — no CMS, no network.
val credits = listOf(
    CreditEntry("Name TBD", "Audio Engine / NDK", "https://..."),   // ⏳ PENDING
    CreditEntry("Name TBD", "UI/UX Designer", "https://...")        // ⏳ PENDING
)

// Hyperlinks open via Intent(Intent.ACTION_VIEW, Uri.parse(url))
```

---

## Section 5: Game Logic & Audio Engine

### 5.1 Timing Constants

```kotlin
// /data/TimingConstants.kt
object TimingConstants {
    const val BPM                  = 80
    const val MS_PER_BEAT          = 60_000L / BPM   // 750ms
    const val TOTAL_BEATS          = 16
    const val COUNT_IN_BEATS       = 4
    const val TOTAL_GAME_DURATION_MS = TOTAL_BEATS * MS_PER_BEAT  // 12_000ms

    // Scoring windows (±ms from beat timestamp)
    const val WINDOW_PERFECT_MS    = 40L    // ±40ms = Perfect
    const val WINDOW_GOOD_MS       = 80L    // ±41ms to ±80ms = Good
    // > ±80ms = Miss

    // Miss detection: if a QUARTER_NOTE's timestamp passes with no tap
    // within WINDOW_GOOD_MS + one processing frame, it's auto-scored MISS.
    const val MISS_DETECTION_GRACE_MS = WINDOW_GOOD_MS + 16L  // ~96ms
}
```

### 5.2 Scoring Algorithm

```kotlin
// GameViewModel.kt — onUserTap()
fun onUserTap() {
    if (gameState.value != GameState.PLAYING) return

    val tapTimeMs: Long = RhythmBridge.nativeGetCurrentTimestampMs()

    // Find the nearest PENDING QUARTER_NOTE within scoring range
    val candidate = noteEvents
        .filter { it.type == NoteType.QUARTER_NOTE && it.result == NoteResult.PENDING }
        .minByOrNull { abs(it.timestampMs - tapTimeMs) }

    if (candidate == null) {
        // Tap during a rest or after all notes are judged = False Tap
        recordFalseTap(tapTimeMs)
        triggerHaptic(HapticType.MISS)
        return
    }

    val delta = abs(candidate.timestampMs - tapTimeMs)

    val result = when {
        delta <= TimingConstants.WINDOW_PERFECT_MS -> NoteResult.PERFECT
        delta <= TimingConstants.WINDOW_GOOD_MS    -> NoteResult.GOOD
        else -> {
            // Tap is too early or too late for nearest note
            recordFalseTap(tapTimeMs)
            triggerHaptic(HapticType.MISS)
            return
        }
    }

    candidate.result = result
    updateNoteResults()

    val hapticType = if (result == NoteResult.PERFECT) HapticType.PERFECT else HapticType.GOOD
    triggerHaptic(hapticType)
    emitFeedback(result)
}
```

**Miss Auto-Detection (runs on beat tick callback):**
```kotlin
// Called by JNI beat callback, on Main thread via Handler
fun onBeatTick(beatIndex: Int, timestampMs: Long) {
    // Check all previous PENDING QUARTER_NOTEs that are now past MISS_DETECTION_GRACE_MS
    noteEvents
        .filter { it.type == NoteType.QUARTER_NOTE && it.result == NoteResult.PENDING }
        .filter { timestampMs - it.timestampMs > TimingConstants.MISS_DETECTION_GRACE_MS }
        .forEach { note ->
            note.result = NoteResult.MISS
            emitFeedback(NoteResult.MISS)
        }

    updatePlayheadFraction(beatIndex, timestampMs)

    if (beatIndex >= TimingConstants.TOTAL_BEATS - 1) {
        onGameComplete()
    }
}
```

### 5.3 Playhead Position Calculation

```kotlin
// Smooth playhead movement using interpolation between beat timestamps.
// Update rate: every audio callback (~5ms at low latency), posted to UI thread.

fun updatePlayheadFraction(currentTimeMs: Long) {
    val fraction = currentTimeMs.toFloat() / TimingConstants.TOTAL_GAME_DURATION_MS.toFloat()
    _playheadFraction.value = fraction.coerceIn(0f, 1f)
}
```

### 5.4 C++ Engine Specification (RhythmEngine.cpp)

```cpp
// RhythmEngine.h — public interface
class RhythmEngine : public oboe::AudioStreamDataCallback {
public:
    bool init(int32_t sampleRate, int32_t framesPerBurst);
    void startCountIn();   // plays 4 metronome clicks, fires JNI beat callbacks
    void startPlayback();  // plays 16-beat loop, fires JNI beat callbacks
    void stop();
    void destroy();
    int64_t getCurrentTimestampMs();

    oboe::DataCallbackResult onAudioReady(
        oboe::AudioStream* stream,
        void* audioData,
        int32_t numFrames) override;

private:
    std::shared_ptr<oboe::AudioStream> mAudioStream;
    // WAV sample data loaded into memory buffer at init
    std::vector<float> mClickSampleData;
    int32_t mSampleRate;
    int64_t mStreamStartTimeNs;     // nanoseconds from stream open
    int32_t mCurrentBeat = -1;
    bool mIsPlaying = false;
    bool mIsCountIn = false;

    // Fires JNI callback to Kotlin
    void fireJniBeatCallback(int beatIndex, int64_t timestampMs);
};
```

**Oboe stream configuration:**
```cpp
oboe::AudioStreamBuilder builder;
builder.setPerformanceMode(oboe::PerformanceMode::LowLatency)
       ->setSharingMode(oboe::SharingMode::Exclusive)
       ->setFormat(oboe::AudioFormat::Float)
       ->setChannelCount(oboe::ChannelCount::Mono)
       ->setSampleRate(sampleRate)
       ->setFramesPerDataCallback(framesPerBurst)
       ->setDataCallback(this);
```

### 5.5 Haptic Feedback

```kotlin
// /ui/HapticController.kt
// Uses VibrationEffect (API 26+) via Vibrator or VibratorManager (API 31+)

enum class HapticType { PERFECT, MISS }

class HapticController(context: Context) {
    private val vibrator = if (Build.VERSION.SDK_INT >= 31) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
            .defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun vibrate(type: HapticType) {
        val effect = when (type) {
            HapticType.PERFECT -> VibrationEffect.createOneShot(30L, 80)   // short, medium
            HapticType.MISS    -> VibrationEffect.createOneShot(80L, 255)  // longer, strong
        }
        vibrator.vibrate(effect)
    }
}
```

---

## Section 6: Game State Machine

```
┌──────────────────────────────────────────────────────────────┐
│                        STATE MACHINE                         │
│                                                              │
│   App Launch                                                 │
│       │                                                      │
│       ▼                                                      │
│   [SPLASH] ──(engine init complete)──► [IDLE]               │
│                                            │                 │
│                                   (any tap on screen)        │
│                                            │                 │
│                                            ▼                 │
│                                       [COUNT_IN]             │
│                              (4 Oboe beats fire, no scoring) │
│                                            │                 │
│                               (beat 4 callback received)     │
│                                            │                 │
│                                            ▼                 │
│                                       [PLAYING]              │
│                          (16 beats, scoring active)          │
│                              │                    │          │
│                      (beat 16 fires)        (all notes       │
│                              │               judged)         │
│                              ▼                               │
│                          [SUMMARY]                           │
│                              │                               │
│                         (Retry tap)                          │
│                              │                               │
│                              ▼                               │
│                           [IDLE]  (new pattern generated)    │
└──────────────────────────────────────────────────────────────┘
```

---

## Section 7: Build Order (Phase-Locked)

```
Phase 1: Project Foundation & NDK Setup
  - Create Android Studio project (minSdk 26, Jetpack Compose, Kotlin DSL)
  - Add Oboe dependency (Prefab) to build.gradle.kts
  - Configure CMakeLists.txt
  - Write stub RhythmEngine.cpp that does nothing (just compiles)
  - Write RhythmBridge.kt with all external fun declarations
  - Verify: app compiles and launches without crash on physical device
  ✅ Deliverable: app opens to blank screen, no crash. NDK .so loads.

Phase 2: Audio Engine (Oboe Metronome)
  - Implement MetronomePlayer.cpp: load WAV from assets, play via Oboe
  - Implement RhythmEngine.cpp: beat scheduling loop at 80 BPM
  - Implement JNI callbacks: onBeatTick fires to Kotlin on every beat
  - Wire SplashScreen: call nativeInit with correct sampleRate & framesPerBurst
  - Test: 4-beat count-in plays on physical device with audible metronome click
  ✅ Deliverable: tap SplashScreen → hear 4 click count-in on physical device

Phase 3: Static UI & Design Tokens
  - Implement all tokens: Color.kt, Type.kt, Dimensions.kt
  - Implement SplashScreen with co-brand layout (placeholder vector drawables OK)
  - Implement GameScreen skeleton: 40/60 split, dark background
  - Implement NotationCanvas with static staff lines (no notes yet)
  - Implement TapZone with tap detection (log to Logcat only)
  ✅ Deliverable: GameScreen shows 5 staff lines in 40% zone. Tap zone logs to Logcat.

Phase 4: Notation Rendering
  - Implement PatternGenerator (16 NoteEvents, mixed notes/rests)
  - Implement drawQuarterNote() in NotationCanvas
  - Implement drawQuarterRest() in NotationCanvas
  - Render all 16 NoteEvents at correct X positions on canvas
  - No playhead yet — just static score
  ✅ Deliverable: GameScreen shows full static score: 16 beats of notes/rests.

Phase 5: Synchronization (Playhead + State Machine)
  - Wire onBeatTick JNI callback → GameViewModel.onBeatTick()
  - Implement playheadFraction StateFlow and continuous update
  - Implement drawPlayhead() with glow effect
  - Implement full GameState machine (IDLE → COUNT_IN → PLAYING → SUMMARY)
  - Implement CountInOverlay (digits 1-4 timed to beats)
  - Test: playhead moves smoothly across 16 beats, synchronized to Oboe audio
  ✅ Deliverable: tap to start → count-in digits → playhead sweeps across score in sync

Phase 6: Scoring & Feedback
  - Implement GameViewModel.onUserTap() with full scoring algorithm
  - Implement auto-Miss detection on beat tick
  - Implement NoteResult → color change in NotationCanvas
  - Implement FeedbackText (Perfect/Good/Miss/False! pop-ups)
  - Implement HapticController (Perfect = short, Miss = long)
  - Test: each note judged correctly vs Oboe timestamp on physical device
  ✅ Deliverable: full gameplay loop works with visual + haptic feedback

Phase 7: Summary & Polish
  - Implement SummaryOverlay with score breakdown + Retry
  - Implement InstructionOverlay ("Tap Anywhere to Start")
  - Implement CreditsScreen (hardcode team data)
  - Remove all debug Logcat statements from production paths
  - Ensure no debug text, ticket numbers, or dev annotations appear on-screen
  ✅ Deliverable: complete gameplay loop from splash to summary to retry

Phase 8: QA & Calibration
  - Test on minimum 3 distinct physical Android devices (different manufacturers)
  - Calibrate scoring windows on each device: verify ±40ms/±80ms windows feel correct
  - Confirm audio latency < 30ms on modern devices (Pixel / Samsung flagship)
  - Confirm playhead visual sync matches audio within 1 frame (16ms tolerance)
  - Verify haptic fires < 10ms after tap detection
  - Verify no ANR (Application Not Responding) during gameplay
  - Verify TapZone never blocks NotationCanvas rendering
  ✅ Deliverable: signed APK ready for Play Store internal testing track
```

**⛔ Hard Rules:**
```
⛔ RULE 1 (Colors):       Never hardcode hex values outside Color.kt.
⛔ RULE 2 (Audio):        Oboe is the ONLY clock. Never use System.currentTimeMillis()
                           for scoring — always use nativeGetCurrentTimestampMs().
⛔ RULE 3 (Threading):    JNI beat callbacks arrive on the AUDIO thread.
                           Always post to Main thread via Handler before touching StateFlow.
⛔ RULE 4 (Assets):       metronome_click.wav must be .WAV only. Reject any PR that adds MP3/OGG.
⛔ RULE 5 (Emulator):     All audio and scoring tests MUST run on physical device.
⛔ RULE 6 (Scope):        Zero network calls. Zero auth. Zero scrolling Canvas. Zero iOS.
⛔ RULE 7 (Canvas):       All notation drawn via Canvas API. Zero PNG/SVG for game elements.
⛔ RULE 8 (Z-Index):      FeedbackText and CountInOverlay always rendered above NotationCanvas
                           in Compose hierarchy — never below.
⛔ RULE 9 (Phase Gate):   Do NOT proceed to next phase without physical device compile + test.
```

---

## Section 8: Constraints Reference (Hard Locked)

| Constraint | Value | Rationale |
|---|---|---|
| Time Signature | 4/4 only | Simplifies beat grid calculation |
| Note Types | Quarter Note + Quarter Rest only | Eliminates complex subdivision logic |
| Tempo | 80 BPM (static) | Fixed `MS_PER_BEAT = 750ms`, no tempo engine needed |
| Duration | 4 bars = 16 beats | All notes fit on screen, no scroll logic |
| Persistence | ViewModel in-memory only | No Jetpack DataStore until Level 2 |
| Network | None | Fully offline, zero Retrofit |
| Platforms | Android only, minSdk 26 | Oboe API requires API 21+, haptics need API 26 |
| Perfect Window | ±40ms | Industry standard for rhythm games |
| Good Window | ±41ms to ±80ms | Forgiving for beginners |
| Miss | >±80ms or no tap | Auto-scored on next beat tick |

---

## Section 9: Open Questions

| # | Question | Status | Impact if Unresolved |
|---|---|---|---|
| 1 | Final names and portfolio URLs for CreditsScreen entries | ⏳ PENDING | Credits screen shows placeholder text — low priority for MVP |
| 2 | Final vector drawables for co-brand logos (developer + client) | ⏳ PENDING | SplashScreen uses placeholder colored rectangles until resolved |
| 3 | Confirm WAV click asset is ready (woodblock / hi-hat / mechanical) | ⏳ PENDING | Blocks Phase 2 — audio engine cannot be validated without this asset |
| 4 | Physical test device list confirmed by team? (at least 3 devices) | ⏳ PENDING | Phase 8 QA cannot begin |
| 5 | Play Store developer account ready? (for Phase 8 internal track) | ⏳ PENDING | Does not block development, only deployment |
| 6 | PatternGenerator seed strategy — same pattern per retry session, or new? | ⚠️ PARTIAL | Current spec: same seed per session, new seed on fresh open. Confirm. |
| 7 | Should false taps on REST beats also trigger haptic? | ⚠️ PARTIAL | Current spec: yes (MISS haptic). Confirm UX intent. |
| 8 | Minimum Android device target for co-brand logo vector compliance | ✅ RESOLVED | API 26+ guarantees VectorDrawable support without compatibility issues |

---

## Section 10: Reference Implementations

| Resource | URL | Usage |
|---|---|---|
| Google Oboe RhythmGame sample | `github.com/google/oboe` → `samples/RhythmGame` | Extract `Game.cpp`, `Player.cpp` for audio scheduling + scoring logic. Replace their OpenGL UI with Jetpack Compose Canvas. |
| osu!lazer HitWindows | `github.com/ppy/osu` → search `HitWindows.cs` | Translate scoring window math to `GameViewModel.kt`. Study `HitObject` structure for `NoteEvent` design. |
| Android NDK audio-echo | `github.com/android/ndk-samples` → `audio-echo` | Reference for JNI bridge boilerplate: `external fun` declarations, `System.loadLibrary`, callback registration. |

---

*PRD_RhythmTrainer_MVP_Level1.md — v1.0 — Generated May 17, 2026*  
*All sections complete. No ⏳ items block Phase 1–6. Phase 7 blocked only on co-brand assets (non-critical path).*
