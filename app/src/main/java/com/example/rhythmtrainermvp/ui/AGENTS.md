# UI Layer — AGENTS.md

**Package:** `com.example.rhythmtrainermvp.ui`
**Stack:** Jetpack Compose (Material 3 · Canvas)

## OVERVIEW

Compose-only UI layer for the rhythm trainer. Dark theme, Canvas-based notation rendering, tap interaction. All state consumed from `GameViewModel` StateFlows — no local UI state management.

## STRUCTURE

```
ui/
├── components/                     # Reusable Compose composables
│   ├── NotationCanvas.kt          # Canvas: 5 staff lines + placeholder for notes/playhead
│   ├── TapZone.kt                 # Box + detectTapGestures → onTap
│   ├── FeedbackText.kt            # Pill-shaped scoring popup (animated)
│   └── CountInOverlay.kt          # Centered digit + scrim
├── screens/                        # Full-screen composables
│   ├── GameScreen.kt              # Main game: 40/60 split
│   ├── SplashScreen.kt            # Startup (stub)
│   ├── SummaryOverlay.kt          # Post-game score card
│   └── CreditsScreen.kt           # Attribution links
├── theme/                          # Compose theming
│   ├── Color.kt                   # ⛔ Single source for ALL colors
│   ├── Type.kt                    # Typography (Inter family)
│   ├── Dimensions.kt              # Spacing, hit targets, playhead
│   └── Animations.kt              # Duration + easing constants
└── HapticController.kt            # Vibrator (API 26+ / 31+)
```

## WHERE TO LOOK

| Task | Location |
|------|----------|
| Change colors | `theme/Color.kt` (⛔ never in components) |
| Staff notation rendering | `components/NotationCanvas.kt` |
| Tap handling | `components/TapZone.kt` |
| Game layout | `screens/GameScreen.kt` |
| Score display | `screens/SummaryOverlay.kt` |
| Haptic feedback | `HapticController.kt` |
| Animation timing | `theme/Animations.kt` |
| Typography | `theme/Type.kt` |
| Spacing constants | `theme/Dimensions.kt` |

## CONVENTIONS

### Canvas Drawing
- `NotationCanvas` uses `drawLine`/`drawCircle` directly on Compose Canvas — no custom `DrawModifier`
- Five staff lines drawn proportionally
- Placeholder for `drawQuarterNote()` + `drawQuarterRest()` + `drawPlayhead()` (Phase 4–5)

### Tap Handling
- `TapZone` exposes `onTap` callback — GameScreen wires it to ViewModel
- No gesture complexity beyond `detectTapGestures { onTap() }`

### Screens
- `GameScreen`: vertical `Column` — 40% top (display zone), 60% bottom (tap zone)
- `SplashScreen`: placeholder `LaunchedEffect` for `nativeInit` wiring (Phase 1/3)
- `SummaryOverlay`: fully implemented card (PERFECT/GOOD/MISS rows, Retry button)
- `CreditsScreen`: fully implemented (hardcoded entries with `AnnotatedString` links)

### Overlay Pattern
- Render in a `Box` overlay layer
- Semi-transparent scrim background
- `CountInOverlay`: single large digit on scrim
- `SummaryOverlay`: Card with score rows

### Feedback Text
- `FeedbackText` takes `NoteResult?` + `timestampMs`
- Pill-shaped `Surface(shape = RoundedCornerShape(50))`
- Color-coded: `HitPerfect` (Neon Green) for PERFECT, `HitGood` (Amber) for GOOD, `HitMiss`/`FalseTap` (Vibrant Red) for MISS
- Placeholder for fade-in/out animation (Phase 6)

### Haptic Controller
- API-level branching: `if (SDK >= 31)` → `VibratorManager`, else legacy `Vibrator`
- `HapticType` enum: `PERFECT` (30ms/80amp), `MISS` (80ms/255amp)
- Not yet wired to scoring (Phase 6)

## ANTI-PATTERNS

| ❌ | Fix |
|---|-----|
| Hardcoded hex in Composables | Use `RhythmColors.*` from `theme/Color.kt` |
| Inline dimensions | Use `RhythmDimensions.*` |
| `Color(0x...)` in components | Route through `theme/Color.kt` |

## KNOWN ISSUES

- `CountInOverlay.kt:30` — hardcoded `Color(0x60121212)`; should use `RhythmColors`
- `SplashScreen.kt` — placeholder `Text("Rhythm Trainer")` instead of brand assets
- `NotationCanvas.kt` — stubs for note/playhead drawing (Phases 4–5)
- `FeedbackText.kt` — animation stubs (Phase 6)
- `CountInOverlay.kt` — animation stubs (Phase 5)
