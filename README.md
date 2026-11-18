# Quantum

**Quantum Alpha 0.3 — Quantum Fluid** — native Android assistant foundation with a user-controlled microphone and speech-to-text.

## Current features
- First-launch local profile (device-only; not cloud authentication).
- Quantum Atlantic identity with a concentric geometric logo.
- Redesigned native Compose home, onboarding, settings, model and subscription presentation.
- Redesigned in-app assistant pill with spring expansion and active-state logo halo.\n- Optional system-wide floating capsule, user-enabled through Android's overlay permission; compact and translucent while Quantum is in the background.
- Microphone permission requested only after the user taps the mic.
- Android speech-recognition service returns a transcript in the app.
- Listening stops when the user taps Stop, closes the panel, the app backgrounds, or the silence timer expires.
- Local model tier selector: Meteor / Comet / Nova. Selection is saved; model files are not bundled.
- Supernova Pro plan preview. Google Play billing is not configured; purchases are disabled.
- Settings persist locally.

## Important limitations
- Recognized text is shown, but no conversational AI provider is connected.
- Speech recognition availability and offline language packs depend on the Android device. The system recognition service may use the network.
- No local LLM is bundled; Meteor / Comet / Nova are selectable profiles only.
- The first-launch profile is local-only, not secure account authentication or cross-device sync.
- Subscription cards are informational only until Play Console products and billing integration are configured.
- Device-control actions, TTS, reminders, macros, widgets and notification summaries are not implemented.
- The system overlay is a lightweight capsule, not a full assistant runtime. Tapping its mic affordance opens Quantum; recording is started separately inside the app.\n- The overlay foreground service may be stopped by Android/OEM power management; it is not guaranteed to survive force-stop or reboot.

## Design handoff
- [Interactive HTML source and visual previews](../QUANTUM_DESIGN_SOURCE.html)
- [Quantum Atlantic design system](docs/QUANTUM_DESIGN_SYSTEM.md)
- [Design implementation handoff](docs/DESIGN_HANDOFF.md)

## Build and test
Requires JDK 17 and Android SDK platform 35.

```bash
./gradlew clean testDebugUnitTest
./gradlew assembleDebug
```

A green GitHub Actions check means CI compiled the project, ran unit tests and produced a debug APK; it does not replace device testing.

See [PRODUCT_SPEC](docs/PRODUCT_SPEC.md), [ARCHITECTURE](docs/ARCHITECTURE.md) and [ROADMAP](docs/ROADMAP.md).
\nSee [Quantum Fluid overlay implementation](docs/QUANTUM_FLUID_OVERLAY.md) for Android permissions, behavior and limitations.\n