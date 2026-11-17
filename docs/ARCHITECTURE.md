# Quantum — Technical Architecture

**Version:** Quantum Alpha 0.1  
**Status:** Proposed architecture; implementation decisions must be validated with Android prototypes.

## High-level modules

- app/ — application shell, navigation, lifecycle, dependency wiring.
- ui/ — Compose screens, design system, accessibility.
- floating/ — overlay button, expanded panel, animation and state machine.
- audio/ — microphone session, audio focus, speech activity/silence detection.
- speech/ — speech-to-text and text-to-speech adapters.
- assistant/ — conversation orchestration, intent interpretation, tool routing.
- ai/cloud/ — provider abstraction and cloud adapters.
- ai/local/ — local inference abstraction and model lifecycle.
- devicecontrol/ — allowlisted Android actions and confirmation workflow.
- memory/ — conversation history and optional long-term memory.
- voices/ — voice catalog, licensing metadata, downloads and queue.
- billing/ — Google Play Billing integration for Supernova.
- data/ — persistence, settings, secure storage and repositories.

## Core request flow

1. User taps the floating star to expand the panel.
2. User taps the panel microphone control.
3. Audio session requests/uses the required Android microphone permission and foreground execution where required.
4. Speech recognition produces a transcript.
5. Assistant orchestrator classifies the request: conversational response, cloud reasoning, local/offline command, or device action.
6. Device actions are converted into typed, allowlisted action proposals.
7. UI presents the exact action and asks for confirmation.
8. Only after confirmation does the action executor invoke the relevant Android API or supported intent.
9. Result is reported to the user and optionally added to conversation history according to memory settings.

## Floating panel state machine

States:
- COLLAPSED
- EXPANDED_IDLE
- LISTENING
- PROCESSING
- AWAITING_CONFIRMATION
- SPEAKING
- ERROR

Transitions:
- COLLAPSED + star tap -> EXPANDED_IDLE
- EXPANDED_IDLE + mic tap -> LISTENING
- LISTENING + transcript finalized -> PROCESSING
- LISTENING + 15 seconds silence -> stop capture -> COLLAPSED
- PROCESSING + action proposal -> AWAITING_CONFIRMATION
- AWAITING_CONFIRMATION + approve -> execute -> SPEAKING or EXPANDED_IDLE
- AWAITING_CONFIRMATION + reject -> EXPANDED_IDLE
- Any active state + explicit close -> stop audio safely -> COLLAPSED

The silence timer must reset when speech is detected. It must not cut off a user who is still speaking. The UI must expose a manual stop control.

## Android constraints

- Use Kotlin and Jetpack Compose for the native UI.
- Use Android-supported overlay and foreground-service patterns; do not assume an overlay can run invisibly or indefinitely.
- Request only necessary permissions, at the point of use.
- Do not use Accessibility Service as a blanket substitute for public Android APIs. If a feature genuinely requires accessibility, explain its purpose and obtain explicit user opt-in in system settings.
- Prefer public intents and supported APIs. Some system settings cannot be toggled directly by third-party apps.
- Respect Android background execution, microphone, notification, and foreground-service restrictions.

## Model runtime

Create a provider/runtime interface so Meteor, Comet, and Nova are selectable implementations rather than hard-coded UI labels.

Device recommendation should consider total and currently available memory, Android version and ABI, CPU/GPU/NPU support where available, thermal and battery constraints, model file size, and context requirements.

Model artifacts must be downloaded separately, checksum-verified, versioned, and accompanied by license metadata. Avoid committing large model binaries to Git.

## Cloud provider abstraction

Keep cloud provider credentials out of the Android client. Use a secure backend or a provider design that does not embed privileged API secrets in the APK. Define quotas and usage accounting server-side for free and Pro plans.

## Data and privacy

- Keep short-lived microphone audio in memory where possible; do not save raw recordings by default.
- Separate conversation history from long-term memory.
- Provide explicit controls to inspect, export, and delete stored memory/history.
- Store sensitive tokens using Android Keystore-backed mechanisms.
- Document what is sent to cloud providers before first use.

## Testing priorities

1. Overlay lifecycle and state transitions.
2. Microphone permission denial/revocation and interruption handling.
3. Silence timeout and speech-continuation behavior.
4. Confirmation gating for every device action.
5. Offline fallback and cloud-to-local transition.
6. Model memory pressure and thermal behavior.
7. Voice download queue recovery and checksum verification.
8. Billing entitlement validation and restore purchases.
