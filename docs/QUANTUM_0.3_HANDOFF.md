# Quantum Alpha 0.3 — Developer Handoff

## What changed
- Added a genuine Android system overlay using WindowManager TYPE_APPLICATION_OVERLAY.
- Added a user-enabled special-use foreground service and persistent notification.
- Added compact background behavior: 62% opacity, reduced size, and a higher position.
- Added animated compact/expanded geometry transitions.
- Added press feedback and long-press dismissal.
- Added an “Остров” entry point to the home screen.
- Kept the existing Android SpeechRecognizer and PanelController; microphone capture remains explicit and in-app.
- Bumped app version to 0.3.0-alpha / versionCode 3.

## Files changed
- app/src/main/AndroidManifest.xml
- app/build.gradle.kts
- app/src/main/java/com/quantum/assistant/MainActivity.kt
- app/src/main/java/com/quantum/assistant/ui/home/HomeScreen.kt
- app/src/main/java/com/quantum/assistant/floating/QuantumIslandService.kt

## Functional behavior
1. Tap “Остров” in Quantum.
2. Grant Android “Display over other apps”.
3. The overlay starts as a foreground service with an ongoing notification.
4. When Quantum leaves the foreground, MainActivity sends COMPACT.
5. When Quantum returns, MainActivity sends RESTORE.
6. Tap the island to open Quantum; long-press it to dismiss.

## Still unfinished
- Device-level testing on Android 14 / Lenovo Dimensity 8300.
- Direct microphone control from overlay (currently opens app; permission and recording remain Activity-owned).
- Robust edge snapping / user drag positioning and per-app placement exclusions.
- AI provider, local LLM, TTS, account backend, Play Billing and real model downloads remain unimplemented.
- System surfaces that prohibit overlays cannot be covered.

## Build
- JDK 17
- Android SDK platform 35
- ./gradlew clean testDebugUnitTest
- ./gradlew assembleDebug

## Risks
- OEM battery optimization can stop foreground services.
- User must grant overlay permission manually.
- Android may hide overlays on sensitive screens.
- Special-use foreground service requires the manifest declaration and platform approval/policy compliance.
- The app has not been physically tested on the target tablet in this handoff.
