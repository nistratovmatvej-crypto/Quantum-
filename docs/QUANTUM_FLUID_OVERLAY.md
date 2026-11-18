# Quantum Fluid — overlay implementation notes

## Implemented in Alpha 0.3.0

- User-controlled Android system overlay, separate from the in-app Compose island.
- Permission is requested through Android's "Display over other apps" settings.
- The overlay is hosted by a foreground service using the Android special-use foreground-service type.
- When Quantum is foreground, the service hides its system bubble so the in-app island remains the primary control.
- When Quantum moves to the background, a compact, semi-transparent bubble appears near the top edge.
- A tap expands the bubble. The microphone affordance opens Quantum; recording still requires a separate microphone tap and the existing runtime RECORD_AUDIO permission.
- The bubble can be dragged and can be disabled from Settings.
- A persistent notification explains that the overlay is active and the microphone is not listening.

## Safety and platform boundaries

- The overlay never starts microphone capture. Android's microphone privacy indicator and runtime permission remain in control.
- The existing speech recognizer is still app-scoped. The overlay does not capture audio while another app is in front.
- Android may stop a foreground service under system pressure or user action. Users can stop it from Quantum settings or Android system controls.
- OEM battery management may affect persistence and should be tested on physical devices.
- The system overlay requires explicit user approval. It is not Apple's Dynamic Island and does not use private APIs.
- Google Play distribution requires a policy review of the declared special-use foreground-service purpose. The service is user-enabled and limited to a visible assistant bubble.

## Known limitations

- The overlay is a native Canvas-rendered capsule, not a Compose surface. This keeps the always-on surface light.
- Its tap-to-mic affordance opens the app; the user then starts recording with the in-app microphone button. This avoids starting microphone capture from a background overlay.
- The overlay does not yet synchronize the in-app panel's live listening/processing states.
- The service does not guarantee survival after force-stop, reboot, or OEM task killing. Boot auto-start is intentionally not enabled.
- Verify position, insets, cutouts, gesture navigation and battery behavior on the target Lenovo Android 14 tablet.

## Build

```bash
./gradlew clean testDebugUnitTest
./gradlew assembleDebug
```
