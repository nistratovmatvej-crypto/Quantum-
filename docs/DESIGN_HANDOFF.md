# Quantum Premium UI/UX Redesign — Handoff

## Delivered in this revision
- Replaced the visual palette with Quantum Atlantic tokens.
- Refined the concentric mark and launcher background.
- Redesigned existing Home, Welcome, Settings and Plans screens in native Compose.
- Kept PanelController, Android SpeechRecognizer, local profile, SettingsRepository and tier selection.
- Preserved in-app-only Dynamic Island and explicit microphone activation.
- Added QUANTUM_DESIGN_SOURCE.html with interactive visual previews and embedded Kotlin/XML source.

## Functional
- Local profile onboarding remains device-only.
- Microphone permission is requested after mic tap.
- SpeechRecognizer lifecycle and transcript remain wired to PanelController.
- Assistant name, speech speed, memory mode and selected tier persist locally.
- Tier selection is a saved preference only.
- Pro screen remains informational; billing is not configured.

## Not implemented / backend required
- Real account registration, sign-in and password recovery.
- Cloud AI provider and text chat.
- Bundled/downloadable Meteor, Comet or Nova models.
- Model download manager and verified benchmarks.
- TTS / voice library.
- Google Play Billing, purchase and restore.
- Reminder service, memory engine and device action executor.
- System-wide overlay. Current assistant pill is inside the app window.

## Architecture decisions
- Keep Kotlin + Compose and current navigation.
- Keep state machine in PanelController; UI observes StateFlow.
- Keep microphone permission at the HomeScreen action boundary.
- Keep SettingsRepository as the persistence seam.
- No WebView; HTML is documentation and visual review only.

## Risks / verification
- No emulator/device screenshot capture was available during this edit.
- CI build and unit tests must run on the branch before merge.
- Original logo image was not present in repository files at inspection time; the native mark follows the supplied concentric design description.
- Authentication and payments must not be represented as functional until providers are integrated.

## Next stage
1. Run CI and inspect compact/tablet layouts.
2. Add screenshot tests with Paparazzi/Roborazzi or instrumentation.
3. Add a real auth provider after backend selection.
4. Add AI provider abstraction and safe command confirmation.
5. Integrate Play Billing and model download manifests.
