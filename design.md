# design.md — Rhythm Trainer MVP (Level 1)
**For:** Google Stitch UI Generation  
**Platform:** Android Native — Jetpack Compose  
**Version:** 1.0 | May 17, 2026

> **Stitch Prompt Context:** Generate Android Jetpack Compose UI screens for a rhythm music training app. Dark theme, minimal HUD, music game aesthetic. All notation (staff lines, notes, rests, playhead) is drawn programmatically via Canvas — no image assets for game elements. The app has 5 screens/states. Produce one screen per prompt.

---

## 0. Design Language Summary

**Aesthetic:** Dark arcade / music instrument hybrid. Think: the inside of a recording studio at night crossed with a rhythm game HUD. Zero clutter. Every pixel serves timing or feedback.

**Mood:** Focused, precise, slightly tense. Like watching a needle on a VU meter — the interface holds its breath until you tap.

**Primary metaphor:** A moving playhead on a music score. The screen is a score sheet illuminated by a neon cursor. The player must "play" the score with their finger.

---

## 1. Color System

| Token Name | Hex | Usage |
|---|---|---|
| `app-bg` | `#121212` | App background (all screens) |
| `surface` | `#1E1E1E` | Cards, modals, overlays |
| `staff-line` | `rgba(255,255,255,0.30)` | 5 paranada / staff lines |
| `note-default` | `#E0E0E0` | Upcoming / unplayed note heads |
| `playhead` | `#00E676` | The moving vertical time cursor |
| `playhead-glow` | `rgba(0,230,118,0.25)` | Glow halo behind playhead line |
| `hit-perfect` | `#00E676` | Note head color after Perfect hit |
| `hit-good` | `#FFD600` | Note head color after Good hit |
| `hit-miss` | `#FF1744` | Note head color after Miss / False Tap |
| `text-primary` | `#FFFFFF` | Main readable text |
| `text-secondary` | `#9E9E9E` | Labels, captions, subtitles |
| `text-countin` | `#00E676` | Count-in beat digits (1,2,3,4) |
| `scrim-dark` | `rgba(18,18,18,0.80)` | Modal / overlay background tint |
| `divider` | `rgba(255,255,255,0.12)` | Thin divider lines in cards |
| `btn-primary-bg` | `#00E676` | "Retry" and main CTA buttons |
| `btn-primary-text` | `#000000` | Text on primary CTA button |

**Rules:**
- Background is ALWAYS `#121212`. Never white, never grey, never any other dark.
- Never use pure `#000000` for text or backgrounds.
- Neon green `#00E676` is the ONLY accent color. No secondary accent.
- All red is exclusively for negative feedback (Miss). Never decorative.
- All amber/yellow is exclusively for Good hit feedback. Never decorative.

---

## 2. Typography

**Font Family:** `Inter` (via `res/font/inter_*.ttf` — Variable font preferred)  
**Fallback:** `sans-serif`

| Role | Weight | Size | Color | Usage |
|---|---|---|---|---|
| `display-xl` | ExtraBold (800) | 72sp | `text-countin` | Count-in digits (1, 2, 3, 4) |
| `display-md` | Bold (700) | 28sp | `text-primary` | "Tap Anywhere to Start" instruction |
| `headline` | Bold (700) | 22sp | dynamic | Perfect / Good / Miss feedback pop |
| `title` | SemiBold (600) | 20sp | `text-primary` | Summary modal title "Results" |
| `body` | Regular (400) | 16sp | `text-primary` | Score breakdown rows in summary |
| `body-accent` | SemiBold (600) | 16sp | dynamic | Score values in summary (colored) |
| `label` | SemiBold (600) | 14sp | `text-secondary` | Sub-labels, captions |
| `btn` | SemiBold (600) | 18sp | `btn-primary-text` | Button labels |
| `caption` | Regular (400) | 12sp | `text-secondary` | Credits, fine print |

**Letter spacing:** `display-xl` = 0sp. All others = 0.25sp–0.5sp.  
**Line height:** 1.4× font size for body. 1.1× for display sizes.

---

## 3. Layout Architecture

### 3.1 Screen Split (ALL gameplay screens)

```
┌──────────────────────────────────────┐  ← top of screen
│                                      │
│           DISPLAY ZONE               │  40% of screen height
│         (NotationCanvas)             │
│                                      │
├──────────────────────────────────────┤  ← 40% mark
│                                      │
│                                      │
│             TAP ZONE                 │  60% of screen height
│         (blank touch area)           │
│                                      │
│                                      │
└──────────────────────────────────────┘  ← bottom of screen
```

- Display Zone: contains ONLY `NotationCanvas` + floating text overlays (feedback, count-in)
- Tap Zone: completely empty, no buttons, no text, no icons
- The split is a hard constraint — never deviate

### 3.2 Margins & Padding

| Context | Value |
|---|---|
| Screen horizontal padding | 0dp (Canvas fills edge-to-edge) |
| Floating text horizontal padding | 16dp from screen edge |
| Summary card horizontal margin | 24dp from screen edge |
| Summary card internal padding | 24dp |
| Icon button tap target | 48dp × 48dp minimum |

### 3.3 Elevation & Depth

- No Material elevation shadows in gameplay. Everything is flat on dark bg.
- Summary modal uses `scrim-dark` overlay behind the card only.
- Cards use `surface` (#1E1E1E) with 16dp rounded corners, no shadow.

---

## 4. Screen Specifications

---

### SCREEN 1 — Splash Screen

**Purpose:** Load NDK library, display co-branding, transition to Game.

**Layout:**
```
┌──────────────────────────────────────┐
│                                      │
│                                      │
│                                      │
│    ┌──────────┬──┬──────────┐        │
│    │ [DEV     │  │  CLIENT  │        │
│    │  LOGO]   │  │  LOGO]   │        │  ← logos center-aligned, 80dp tall each
│    └──────────┴──┴──────────┘        │
│    "Developed in partnership with"   │  ← 12sp, text-secondary, center
│                                      │
│                                      │
└──────────────────────────────────────┘
```

**Specs:**
- Background: `app-bg`
- Both logos: 80dp height, width proportional (aspect-ratio preserved), `VectorDrawable`
- Separator between logos: 1dp vertical line, `rgba(255,255,255,0.20)`, height = 60dp
- Caption text: `caption` style, `text-secondary`, centered below logos, 12dp margin top
- No loading spinner, no progress bar — silence during load
- Minimum display duration: 1500ms before navigating away
- If engine fails: replace logos with error text `"Audio engine failed to initialize"` in `hit-miss` color + Retry `TextButton` in `playhead` color

**Stitch Prompt:**
> "Android Jetpack Compose splash screen. Dark background #121212. Center of screen: two square logos side by side, separated by a thin vertical divider line. Below logos: small grey caption text 'Developed in partnership with'. No animation, no loading bar. Clean, minimal, professional. Dark music app aesthetic."

---

### SCREEN 2 — Game Screen: IDLE State

**Purpose:** First thing user sees after splash. Score is visible. Waiting for tap.

**Layout:**
```
┌──────────────────────────────────────┐
│  ┌────────────────────────────────┐  │
│  │  ════════════════════════════  │  │  ← staff line 1
│  │  ════════════════════════════  │  │  ← staff line 2
│  │  ● ● ● 𝄽 ● ● 𝄽 ● ● ● 𝄽 ● ●   │  │  ← notes on staff line 3
│  │  ════════════════════════════  │  │  ← staff line 4
│  │  ════════════════════════════  │  │  ← staff line 5
│  └────────────────────────────────┘  │
│                                      │  ← 40% mark
│                                      │
│        Tap Anywhere to Start         │  ← display-md, text-primary, centered
│                                      │
│                                      │
└──────────────────────────────────────┘
```

**Canvas specs:**
- 5 staff lines: white at 30% opacity, 1dp stroke, full canvas width
- All 16 note heads visible: `note-default` (#E0E0E0) ovals
- Playhead: a thin vertical line at X=0% (left edge) in `playhead` color — static/invisible at far left
- No bar lines, no clef, no time signature symbol — MVP only shows notes + staff

**Instruction text:**
- "Tap Anywhere to Start"
- Positioned: bottom-center of Tap Zone (NOT on top of canvas)
- Font: `display-md` (28sp, Bold)
- Color: `text-primary` but at 85% opacity
- Subtle pulse animation: opacity oscillates 70%→100% over 2s, repeat

**Stitch Prompt:**
> "Android Jetpack Compose game screen, IDLE state. Top 40% of screen: dark canvas with 5 horizontal white staff lines (low opacity). Small filled oval note heads in off-white (#E0E0E0) placed on the middle staff line, evenly spaced across the full width — 12 notes and 4 rest symbols alternating randomly. Bottom 60%: completely empty dark area with centered text 'Tap Anywhere to Start' in white, large bold font. Info icon (gear or info) in top-right corner. No other UI elements."

---

### SCREEN 3 — Game Screen: COUNT-IN State

**Purpose:** After first tap. Oboe plays 4 clicks. Show digits 1→2→3→4 to give user tempo reference.

**Layout:**
```
┌──────────────────────────────────────┐
│  [NotationCanvas — notes visible     │
│   but playhead still at left edge]   │
│                                      │
├──────────────────────────────────────┤
│                                      │
│                                      │
│               ③                     │  ← current count-in beat digit
│                                      │    72sp, ExtraBold, #00E676
│                                      │
│                                      │
└──────────────────────────────────────┘
```

**Count-in digit behavior:**
- Digit appears centered in Tap Zone
- Each digit is visible for exactly 750ms (one beat at 80 BPM)
- Sequence: 1 → 2 → 3 → 4
- Entrance animation: scale from 120% → 100%, fade in over 80ms
- Exit: fade out over 80ms as next digit appears
- No pulse/pulsate — hard cut between digits to mirror metronome precision
- Font: `display-xl` (72sp, ExtraBold, `text-countin` = #00E676)

**NotationCanvas during count-in:**
- All notes still visible, no color change
- Playhead remains at X = 0% (far left, barely visible)
- Staff lines remain visible

**Stitch Prompt:**
> "Android Jetpack Compose game screen, COUNT-IN state. Top 40%: dark canvas with music staff and note heads in off-white, unchanged from idle. Bottom 60%: a single large number '3' centered in the empty dark area, rendered in neon green (#00E676), 72sp extra-bold. No other text. No buttons. The number counts down 1-2-3-4 before gameplay starts."

---

### SCREEN 4 — Game Screen: ACTIVE PLAYING State

**Purpose:** Core gameplay. Playhead moves. User taps to notes. Visual feedback fires.

**Layout:**
```
┌──────────────────────────────────────┐
│  ┌────────────────────────────────┐  │
│  │     ╔══Perfect!╗               │  │  ← feedback text (top-center, z-top)
│  │  ══ ║══════════║═════════════  │  │  ← staff
│  │  ✅ 𝄽 ✅  ┃  ●  ● 𝄽  ●  ●    │  │  ← green notes = played, neon line = playhead
│  │  ══ ║══════════║═════════════  │  │
│  │  ════════╠══════════════════   │  │
│  └──────────╫──────────────────── │  │
│             ╫                     │  │  ← playhead extends full height
│             ╫                     │
│             ╫                     │
│             ╫  [Tap Zone]         │
└─────────────╨────────────────────-┘
```

**Playhead specs:**
- A vertical line spanning the FULL screen height (both Display and Tap Zones)
- Width: 3dp solid `playhead` (#00E676) + 8dp `playhead-glow` behind it
- Moves continuously left-to-right from X=0% to X=100% over 12,000ms
- Movement must be perfectly linear — no easing

**Note state colors (applied to note head oval fill):**
- Pending → `note-default` (#E0E0E0)
- Perfect → `hit-perfect` (#00E676) — fills oval solid
- Good → `hit-good` (#FFD600) — fills oval solid
- Miss → `hit-miss` (#FF1744) — fills oval solid
- False Tap (on rest) → red ❌ text/icon appears above the rest glyph, 400ms then fades

**Feedback text ("Perfect!" / "Good!" / "Miss!"):**
- Position: top-center of FULL SCREEN, y = 24dp from top
- Has a pill-shaped semi-transparent background behind text: `rgba(18,18,18,0.70)`, corner radius 20dp, horizontal padding 16dp, vertical padding 6dp
- "Perfect!" → `hit-perfect` color
- "Good!" → `hit-good` color
- "Miss!" → `hit-miss` color
- "False!" → `hit-miss` color
- Appears instantly, visible 300ms, fades out over 400ms
- Only one feedback text visible at a time

**Tap Zone during play:**
- Background: `app-bg` — completely flat, no ripple required (optional subtle ripple at tap point: 40dp radius, `rgba(255,255,255,0.05)`, fades in 100ms)

**Stitch Prompt:**
> "Android Jetpack Compose music rhythm game, ACTIVE gameplay screen. Top 40%: dark canvas with 5 white staff lines (low opacity). Note heads in various colors — some off-white (upcoming), some neon green (perfect hit), some yellow (good hit), one red (missed). A neon green vertical line (the playhead) cuts through the middle of the canvas and extends down through the full screen height, with a soft glow effect. Top-center of screen: a pill-shaped semi-transparent label showing 'Perfect!' in neon green. Bottom 60%: completely empty dark tap zone, full width."

---

### SCREEN 5 — Game Screen: SUMMARY State

**Purpose:** Show score after 16 beats. Present breakdown. Offer Retry.

**Layout:**
```
┌──────────────────────────────────────┐
│  [NotationCanvas — all notes color-  │
│   coded with final results, playhead │
│   at far right edge]                 │
│                                      │
├──────────────────────────────────────┤  ← 40% mark
│  ╔════════════════════════════════╗  │
│  ║           Results              ║  │  ← title-lg, text-primary
│  ║  ─────────────────────────────  ║  │
│  ║  ✅ Perfect       12           ║  │  ← value in hit-perfect color
│  ║  ✅ Good           2           ║  │  ← value in hit-good color
│  ║  ❌ Miss           1           ║  │  ← value in hit-miss color
│  ║  ❌ False Taps     1           ║  │  ← value in hit-miss color
│  ║  ─────────────────────────────  ║  │
│  ║  Accuracy        87.5%         ║  │  ← bold, text-primary
│  ║                                ║  │
│  ║  ┌──────────────────────────┐  ║  │
│  ║  │          RETRY           │  ║  │  ← filled button, btn-primary-bg
│  ║  └──────────────────────────┘  ║  │
│  ╚════════════════════════════════╝  │
└──────────────────────────────────────┘
```

**Overlay:**
- Full-screen `scrim-dark` (`rgba(18,18,18,0.80)`) behind card
- Card: `surface` (#1E1E1E), corner radius 16dp, horizontal margin 24dp, internal padding 24dp
- Card slides up from bottom: 300ms, fast-out-slow-in easing
- NotationCanvas remains visible behind scrim (shows final color state of all notes)

**Score rows:**
- Each row: label on left (`body`, `text-secondary`), value on right (`body-accent`, colored)
- Row height: 44dp minimum
- Divider lines between sections: 1dp, `divider` color

**Retry button:**
- Full width within card
- Height: 52dp
- Background: `btn-primary-bg` (#00E676), corner radius 12dp
- Text: "RETRY", `btn` style, `btn-primary-text` (#000000)
- On press: card slides down and disappears, then new pattern generates, returns to IDLE state
- No "Next Level", no "Share", no other buttons

**Stitch Prompt:**
> "Android Jetpack Compose music game results screen. Top 40%: dark canvas with colored note heads showing game results (green/yellow/red). Bottom 60%: a semi-transparent dark overlay covering the full screen, with a centered card (#1E1E1E, rounded corners 16dp). Card contains: title 'Results', four score rows (Perfect, Good, Miss, False Taps) with colored numbers on the right, a bold accuracy percentage, and a full-width neon green button labeled 'RETRY' with black text. The card slides up from the bottom."

---

### SCREEN 6 — Credits Screen (Settings)

**Purpose:** Static attribution page. Accessible via info icon.

**Layout:**
```
┌──────────────────────────────────────┐
│  ← Back                              │
│                                      │
│  Credits                             │  ← headline, text-primary
│                                      │
│  ┌────────────────────────────────┐  │
│  │  Name                          │  │
│  │  Audio Engine / NDK            │  │  ← label, text-secondary
│  │  github.com/...  ↗             │  │  ← caption, playhead color, tappable
│  └────────────────────────────────┘  │
│                                      │
│  ┌────────────────────────────────┐  │
│  │  Name                          │  │
│  │  UI/UX Designer                │  │
│  │  portfolio.com/...  ↗          │  │
│  └────────────────────────────────┘  │
│                                      │
└──────────────────────────────────────┘
```

**Specs:**
- Background: `app-bg`
- Cards: `surface`, corner radius 12dp, no elevation
- Card padding: 16dp
- Name text: `body` 16sp, `text-primary`
- Role text: `label` 14sp, `text-secondary`
- Link text: `caption` 12sp, `playhead` color (#00E676), underlined
- External link icon (↗): 14dp, `playhead` color, inline with link text
- Back navigation: system back button (no custom back arrow needed for MVP)

**Stitch Prompt:**
> "Android Jetpack Compose static credits/about screen. Dark background #121212. Title 'Credits' in white at top. Two card items below, each showing: a person's name in white, their role in grey, and a clickable portfolio link in neon green with an external link icon. Cards have dark grey backgrounds (#1E1E1E), rounded corners. Clean and minimal."

---

## 5. Component Anatomy

### 5.1 Quarter Note (Canvas-drawn)

```
         │  ← stem, 2dp line, upward, 35dp tall
         │
   ╔═════╗  ← oval head, 14dp wide × 10dp tall, filled solid
   ╚═════╝
```

- Head: `drawOval()`, filled, color = state-dependent
- Stem: `drawLine()` from right-edge of head, going UP
- Stem attached at top-right of oval, not center

### 5.2 Quarter Rest (Canvas-drawn)

```
   ╲
    ╲─
     ─╲
       ╱
      ╱
```

- Rendered as a `Path` approximating the standard quarter rest glyph
- Proportional to note head size
- Color: always `note-default` (#E0E0E0) — does NOT change color on false tap
- On false tap: a separate `❌` text drawn 12dp above the rest's Y position

### 5.3 Staff Lines

- 5 horizontal lines
- Spacing: 10dp between each line
- Stroke: 1dp, `staff-line` color (`rgba(255,255,255,0.30)`)
- Full canvas width (edge-to-edge)
- Static — drawn every frame as background layer

### 5.4 Playhead

```
  ▓ (glow, 8dp wide, rgba(0,230,118,0.25))
  ║ (solid, 3dp wide, #00E676)
```

- Drawn on top of all notes and staff lines
- Extends full height of canvas (and optionally full screen height for visual continuity)
- No rounded caps

---

## 6. Motion & Interaction Specifications

| Interaction | Animation | Duration | Easing |
|---|---|---|---|
| Splash → Game navigation | Fade out splash, fade in game | 300ms | Linear |
| "Tap Anywhere" text pulse | Opacity 70%→100%→70%, loop | 2000ms | Ease-in-out |
| Count-in digit entrance | Scale 120%→100% + fade in | 80ms | Linear |
| Count-in digit exit | Fade out | 80ms | Linear |
| Note color change (hit) | Instant fill change (no tween) | 0ms | — |
| Feedback text appearance | Fade in | 50ms | Linear |
| Feedback text disappearance | Fade out | 400ms | Linear |
| False tap ❌ icon | Fade in 50ms, hold 300ms, fade out 400ms | 750ms total | Linear |
| Summary modal entrance | Slide up from bottom | 300ms | FastOutSlowIn |
| Summary modal exit (Retry) | Slide down | 200ms | Linear |
| Playhead movement | Continuous linear X translation | 12,000ms | Linear (no easing) |

**⛔ Rule:** Playhead movement MUST use linear interpolation. Any easing curve will desync from audio.

---

## 7. Accessibility & Edge Cases

| Scenario | Behavior |
|---|---|
| Small screen (< 360dp width) | Staff lines compress, but 5-line structure maintained. Note ovals may be smaller (minimum 8dp wide). Never clip. |
| Large screen / tablet | 40/60 split maintained. Canvas scales proportionally. Max canvas width = 600dp centered. |
| No vibrator hardware | App detects at runtime, skips haptic silently. No error shown. |
| Note head overlap (unlikely at 16 notes/screen) | Minimum 1dp gap between note heads. Generator guarantees no two notes at same X. |
| Simultaneous notes (not in MVP) | Not possible — Quarter Notes are single-beat, no polyphony in Level 1. |
| User taps during count-in | Taps are ignored. No visual feedback during count-in. |
| User taps after all notes are judged | Treated as false tap, but no new judgment displayed. |

---

## 8. Stitch Prompt Master Reference

Use these prompts verbatim in Google Stitch, one screen at a time:

**Splash:**
> "Android Jetpack Compose splash screen. Background #121212. Center of screen: two rectangular logos side by side, 80dp tall, separated by a 1dp vertical white divider at 20% opacity. Below: small text 'Developed in partnership with' in grey (#9E9E9E). Nothing else. Dark, minimal, professional."

**Game — Idle:**
> "Android Jetpack Compose rhythm game idle screen. Background #121212. Top 40% of screen: a horizontal canvas with 5 thin white staff lines at 30% opacity. On the middle line: 12 small filled oval note heads in #E0E0E0 (off-white), evenly spaced. Small info icon in top-right corner. Bottom 60%: empty dark area, large centered white text 'Tap Anywhere to Start' in bold. Nothing else."

**Game — Count-In:**
> "Android Jetpack Compose rhythm game. Top 40%: dark canvas with 5 white staff lines (30% opacity) and small off-white note heads. Bottom 60%: completely dark with the single number '2' centered, 72sp, extra-bold, neon green #00E676. No other elements."

**Game — Playing:**
> "Android Jetpack Compose rhythm game active state. Top 40%: dark canvas, 5 white staff lines. Note heads colored: some off-white (upcoming), some neon green (perfect), some yellow #FFD600 (good), one red #FF1744 (missed). A neon green #00E676 vertical line (3dp wide, soft glow) crosses through the canvas. Top-center: pill label 'Perfect!' in neon green. Bottom 60%: empty dark tap zone, the neon vertical line continues down through it."

**Game — Summary:**
> "Android Jetpack Compose rhythm game results. Top 40%: dark canvas with all note heads colored (green/yellow/red mix). Full-screen dark overlay at 80% opacity. Centered card (#1E1E1E, 16dp corners, 24dp margin): title 'Results', four rows (Perfect 12 in green, Good 2 in yellow, Miss 1 in red, False Taps 1 in red), bold 'Accuracy 87.5%', full-width green #00E676 button 'RETRY' with black text."

**Credits:**
> "Android Jetpack Compose credits screen. Background #121212. Title 'Credits' in white at top left. Two cards (#1E1E1E, 12dp corners): each card shows a name in white 16sp, a role in grey 14sp, and a clickable link in neon green #00E676 with external link icon. Minimal, dark, no decorations."

---

*design.md v1.0 — Rhythm Trainer MVP Level 1 — Ready for Google Stitch input*
