# Quantum — Product Specification

**Version:** Quantum Alpha 0.1  
**Status:** Initial product baseline

## Identity

- Product name: Quantum
- Platform: Android
- Visual identity: deep navy background, simple white star with a subtle cool gradient and restrained glossy highlight; cyan/blue accents.
- Default assistant name: Quantum
- Default voice direction: calm, polished, Jarvis-inspired character; do not imply an official Marvel voice or use an unlicensed actor/character clone.
- Speech speed: 1.0x
- Communication style: friendly, concise, honest
- Language: follow Android device language by default; allow manual override.

## Floating assistant

1. A compact circular button displays the Quantum star.
2. First tap expands a horizontal floating panel. Microphone remains off.
3. Second tap inside the expanded panel starts microphone capture.
4. Speech is transcribed and sent through the assistant pipeline.
5. After 15 seconds with no detected speech, microphone capture stops and the panel collapses.
6. The user can manually stop listening or collapse the panel at any time.
7. The app must clearly indicate microphone state and respect Android foreground-service and overlay restrictions.

The 15-second timeout is a silence timeout, not a hard limit on the length of a spoken request.

## Assistant capabilities

Quantum is a full conversational AI and device assistant. It should understand intent, ask clarifying questions when needed, and use tools/actions when appropriate.

Potential Android actions include launching apps, opening supported settings screens, creating alarms/timers/reminders, and other capabilities permitted by Android APIs and granted permissions.

**Safety rule:** actions that change device state, access private data, send messages, place calls, delete files, or otherwise have meaningful consequences require a clear confirmation step. Permission prompts must be transparent and initiated only when the feature needs them.

## Intelligence modes

- Cloud AI is available to free and Pro users, subject to different usage limits.
- Local AI supports essential offline commands and lightweight requests.
- Quantum should preserve conversational context when switching between cloud and local modes where feasible.
- Network state and local-model availability should be visible to the user.

### Local model tiers

| Tier | Target device class | Intended trade-off |
|---|---|---|
| Quantum Meteor | Lower-resource devices | Small footprint and lower memory use; reduced reasoning and context capability |
| Quantum Comet | Mid-range devices | Balanced speed, memory use, and capability |
| Quantum Nova | High-end devices | Larger model and stronger local reasoning; higher RAM, storage, and thermal demand |

The app recommends a tier based on measured device capability and available resources, but lets the user choose another compatible tier. Do not promise that a tier will run based only on a chipset name.

## Memory

Four user-selectable modes:

1. Off — do not retain cross-session memory.
2. Dialogue only — retain conversation history.
3. Full — retain useful facts and preferences.
4. Full + configurable — full memory with review, edit, pin, and delete controls.

Recommend a mode based on device/storage capability, but keep memory controls separate from model selection. Memory should be stored independently of model weights and should not require keeping the model resident in RAM.

## Voice library

- 150 free base voices.
- 300 additional voices for Quantum Supernova Pro.
- Voices are presented as named character/persona profiles rather than generic labels where legally permitted.
- Voice language follows the app/device language by default; voice identity and accent are separate selectable properties.
- Users can preview voices before downloading.
- Users can select multiple voices and add them to a download queue.
- Queue supports progress, pause/resume where supported, retry, and cancellation.
- Voice assets are downloaded on demand, not bundled wholesale.

Only use voice assets with verified redistribution and commercial-use rights appropriate to the app. A voice being publicly downloadable or described as free does not establish that it is copyright-free. Character names, likenesses, and voice imitations may require separate permissions. Provide original or licensed alternatives when rights are unclear.

## Quantum Supernova Pro

- Monthly subscription.
- Annual purchase/subscription paid upfront at a lower effective monthly price.
- Same feature entitlement for monthly and annual plans.
- Higher cloud AI usage limits.
- 300 additional voice profiles.
- Further premium features may be added without removing core functionality from free users.

## Default settings

- Name: Quantum
- Voice: Jarvis-inspired (licensed/original voice only)
- Speech speed: 1.0x
- Tone: friendly, concise, honest
- Language: device language
- Floating assistant: manual activation only
- Microphone: off until the user taps the expanded panel
