# Quantum Alpha 0.1.1 — Build report

**Status: GREEN — Android unit tests and debug APK build passed in GitHub Actions.**

- Workflow: https://github.com/nistratovmatvej-crypto/Quantum-/actions/runs/44169653947
- Verified source commit: `46b4a002376e3a510d5ba761d75644914addb34e`
- Wrapper/report commit: `9252b1c530735b2e7bdc3ac8cb40ce88704a9b7b`
- Runner: Ubuntu Linux
- Java: Temurin 17.0.21
- Gradle: 8.10.2
- Android Gradle Plugin: 8.6.1
- Kotlin: 2.0.20
- Android SDK: API 35, Build Tools 35.0.0

## Executed commands

| Command | Result |
|---|---|
| `gradle wrapper --gradle-version 8.10.2 --distribution-type bin` | Passed |
| `./gradlew --version` | Passed |
| `./gradlew --no-daemon clean` | Passed |
| `./gradlew --no-daemon testDebugUnitTest` | Passed — 30 tests, 0 failures |
| `./gradlew --no-daemon assembleDebug` | Passed |
| APK non-empty check | Passed |

The official Gradle Wrapper files are committed in this branch:
- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/gradle-wrapper.jar` (43,583 bytes)
- `gradle/wrapper/gradle-wrapper.properties`

## Debug APK

- Build output: `app/build/outputs/apk/debug/app-debug.apk`
- Size: **9,154,393 bytes** (approximately 8.73 MiB)
- SHA-256: `590197a936ad47336355cd144f55122272beeefa1c6ec27139fcf3a630f8ccd1`
- GitHub Actions artifact: [Quantum-Alpha-0.1.1-debug-apk](https://github.com/nistratovmatvej-crypto/Quantum-/actions/runs/44169653947/artifacts/14367931087)

## Non-fatal build warning

Android packaging could not strip `libandroidx.graphics.path.so`; Gradle packaged the library unchanged. The APK build still completed successfully.

## Scope and limitations

This verifies Android compilation, JVM unit tests and debug APK packaging. It does not mean microphone capture, speech recognition, AI, TTS, device actions, system overlays or billing are implemented. Installation and interactive smoke testing on a physical Android device remain outstanding.
