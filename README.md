# Quantum Alpha 0.3 — Fluid Island

Native Android assistant shell with a premium Quantum Atlantic identity and an opt-in system-wide floating island.

## Current features
- Kotlin + Jetpack Compose native app.
- Existing in-app assistant panel and PanelController state machine retained.
- Android SpeechRecognizer transcription after explicit microphone permission.
- User-enabled Android system overlay implemented with WindowManager TYPE_APPLICATION_OVERLAY.
- Special-use foreground service with persistent notification.
- Compact mode while Quantum is backgrounded: smaller, higher, 62% opacity.
- Restore mode when Quantum returns to foreground.
- Animated island size morph and touch feedback; long-press dismisses it.
- Overlay permission is requested only after the user taps “Остров”.

## Important limitations
- Overlay tap opens the app; microphone permission and capture remain in-app. It does not secretly listen in the background.
- Speech recognition produces a transcript only. No conversational AI provider or TTS is connected.
- Android/OEM battery management may stop a long-running foreground service.
- Android secure/system surfaces may hide overlays.
- Account backend, local LLM weights, real model downloads, Play Billing, reminders and device actions are not implemented.
- No physical-device test is claimed. CI build and tests must be checked for this exact commit.

## Build
Requires JDK 17 and Android SDK platform 35:
```bash
./gradlew clean testDebugUnitTest
./gradlew assembleDebug
```

## Handoff
- [Design/source HTML](QUANTUM_DESIGN_SOURCE.html)
- [Fluid design system](docs/QUANTUM_FLUID_DESIGN_SYSTEM.md)
- [Alpha 0.3 handoff](docs/QUANTUM_0.3_HANDOFF.md)
