# CLAUDE_HANDOFF — Quantum Alpha 0.1 (completion & repair pass)

## Build and test results — NOTHING WAS BUILT OR RUN
The environment had no Android SDK, no Gradle, no Gradle distribution and no network, so `./gradlew assembleDebug`
and `./gradlew testDebugUnitTest` were **not run**. The project is **unverified**: it has never been compiled, and none of the
unit tests have been executed. What I did check: all XML parses; Kotlin brackets balance; I hand-traced the timer tests' virtual-time maths.
Expect possible compile errors on first sync.

## The missing-files report
The previous ZIP I produced listed `MainActivity.kt`, `QuantumApp.kt` and all other sources, but I cannot see what reached you.
This ZIP re-includes everything. Every file is also in `QUANTUM_SOURCE.html`.

## Gradle wrapper: incomplete (honest)
`gradle-wrapper.jar` is **not included**: it is a binary that cannot be fetched or faithfully recreated offline.
Included: `gradle-wrapper.properties` (Gradle 8.10.2), plus `gradlew` / `gradlew.bat` which are **simplified launchers, not the official scripts**.
They fail with a clear message while the jar is missing. Fix with either:
- open the folder in Android Studio (it downloads Gradle from the properties file; no jar needed), or
- run `gradle wrapper --gradle-version 8.10.2` once with a system Gradle to generate the official jar and scripts.

## Versions (chosen to be mutually compatible; unverified by build)
AGP 8.6.1, Gradle 8.10.2, Kotlin 2.0.20 + Compose compiler plugin, Compose BOM 2024.09.03, JDK 17+ (21 fine), compileSdk/targetSdk 35, minSdk 26.
Test-only extra: `kotlinx-coroutines-test` 1.8.1 (needed for virtual-time timer tests).

## What changed
- **PanelController** rewritten: all 7 states, explicit transition table (documented in the file), every invalid event rejected (returns false, state unchanged). One timer job, cancelled before any restart/stop/close. Closing always stops audio.
- **Timer honesty:** it counts as silence detection only if the mic really reports speech activity. With capture unavailable (now) LISTENING is a labelled **preview** ("No audio is captured"). A live mic without speech detection gets no timer, so it can never cut off a speaking user.
- **MicSession**: now reports `availability` and `reportsSpeechActivity`, takes a `MicListener` (speech/transcript/error callbacks) for a future real implementation. `NoOpMicSession` states capture is unavailable. No new permissions.
- **Capability**: replaced RAM-only code with `CapabilityInput` -> `CapabilityAssessor` -> per-tier verdicts + reasons + lists of what was/wasn't measured. Reads RAM, API level, ABI, and (when readable) thermal status, battery %, battery saver. GPU/NPU/storage/benchmarks are explicitly **not measured**. Threshold numbers are **placeholders**.
- **Tier selection** (Automatic / Meteor / Comet / Nova) added to Settings and persisted; no model exists yet, so it only saves the choice.
- **Settings**: repository now uses a `KeyValueStore` seam (SharedPreferences in the app), so persistence is unit-testable; corrupt values fall back to defaults.
- **UI**: panel shows text/controls for all 7 states (Approve/Reject, Stop speaking, Dismiss are real transitions but unreachable until producers exist); live-region announcements; 48dp targets; Home mic status reflects capture availability.
- Added: `gradlew`, `gradlew.bat`, `ThresholdCapabilityAssessorTest`, `SettingsRepositoryTest`, expanded `PanelControllerTest` (full state x event matrix, manual stop, close from every state, preview timeout, speech reset, no timer stacking, mic failure paths).

## What works / is incomplete
Written and expected to work: navigation, star/panel behaviour, settings persistence, device readout and tier verdicts. **Unverified until built.**
Incomplete by design: no real microphone, speech recognition, TTS, AI, memory, voices, billing, device actions, or system overlay.
The panel is in-app only and collapses when the app is backgrounded.

## Known limitations
Hard-coded UI strings; panel not draggable; no language override; PROCESSING/AWAITING_CONFIRMATION/SPEAKING are only reachable from tests today; tier thresholds are invented placeholders.

## Next milestone (Alpha 0.2)
Get a green build and test run first. Then implement a real `MicSession` (RECORD_AUDIO at point of use, interruption handling, real voice-activity detection), add STT, then TTS.
