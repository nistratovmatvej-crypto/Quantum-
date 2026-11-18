# Quantum

**Quantum Alpha 0.2** — native Android assistant foundation with a user-controlled microphone and speech-to-text.

## Current features
- First-launch local profile (device-only; not cloud authentication).
- Deep Atlantic teal / porcelain Quantum identity and concentric logo.
- Expandable in-app Dynamic Island pill. It is not a system-wide overlay.
- Microphone permission is requested only after the user taps the mic.
- Android speech-recognition service returns a transcript in the app.
- Listening stops when the user taps Stop, closes the panel, the app backgrounds, or the silence timer expires.
- Local model tier selector: Meteor / Comet / Nova. Selection is saved; model files are not bundled yet.
- Supernova Pro plan preview. Google Play billing is not configured; purchases are disabled.
- Settings persist locally.

## Important limitations
- The recognized text is shown, but no conversational AI provider is connected yet.
- Speech recognition availability and offline language packs depend on the Android device. The system recognition service may use the network.
- No local LLM is bundled; Meteor / Comet / Nova are selectable profiles only.
- The first-launch profile is local-only, not secure account authentication or cross-device sync.
- Subscription cards are informational only until Play Console products and billing integration are configured.
- Device-control actions, TTS, reminders, macros, widgets and notification summaries are not implemented yet.
- The Dynamic Island control is inside the app, not an always-on system overlay. The microphone is never always listening.

## Build and test
Requires JDK 17 and Android SDK platform 35.

```bash
./gradlew clean testDebugUnitTest
./gradlew assembleDebug
```

GitHub Actions runs the same unit-test and debug-APK build steps on pushes and pull requests. A green check means CI compiled the project, ran unit tests, and produced a debug APK; it does not replace device testing.

## Product direction
See [PRODUCT_SPEC](docs/PRODUCT_SPEC.md), [ARCHITECTURE](docs/ARCHITECTURE.md), [ROADMAP](docs/ROADMAP.md), and [DESIGN_SYSTEM](docs/DESIGN_SYSTEM.md).
