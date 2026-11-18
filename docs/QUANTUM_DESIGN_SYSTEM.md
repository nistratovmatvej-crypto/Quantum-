# Quantum Atlantic — Premium Design System

**Revision:** 2026 / Alpha 0.2 redesign · **Platform:** Native Android, Kotlin, Jetpack Compose.

## Brand
Quantum uses a concentric geometric mark: porcelain outer ring, cyan field and porcelain center. It replaces the former star motif. The current vector and Canvas versions are native approximations of the supplied reference description. The original image itself was not present in the repository at inspection time, so exact pixel sampling has not been possible.

## Color tokens
| Token | Hex | Role |
|---|---|---|
| Background | #071522 | Main app canvas |
| Background elevated | #0B1E2D | Hero and floating surfaces |
| Surface | #102638 | Cards and grouped controls |
| Surface raised | #163449 | Selected and pressed surfaces |
| Surface soft | #0D2435 | Quiet secondary surface |
| Outline | #244354 | Low-contrast separators |
| Accent | #079FC0 | Primary Quantum cyan |
| Accent deep | #078EAE | Gradient depth |
| Accent bright | #35BBD5 | Active states |
| Porcelain | #F4F3EF | Primary text and logo |
| Muted | #91A8B5 | Secondary text |
| Success | #72D6B2 | Confirmed status |
| Warning | #FFC477 | Caution / unavailable |

## Typography
Native system sans-serif through Material typography. Display 42/48sp, headline 32/38sp, section title 20/26sp, body 14–16sp, compact labels 11–13sp. Semibold weights are reserved for hierarchy.

## Spacing and shape
Base rhythm: 4dp. Common insets: 20–24dp. Compact controls: 44–48dp minimum touch target. Cards: 22–28dp radius. Hero surfaces: 30–34dp. Dividers are subtle and group content only.

## Component rules
- One primary action per screen.
- Cyan indicates action or state, not decoration.
- Cards group related information; avoid nested cards.
- State and unavailable functionality are described honestly.
- Touch targets should be at least 48dp where practical.
- The assistant pill is in-app, not a system overlay.
- The microphone starts only after an explicit tap.

## Motion
Compose-native size and alpha transitions, restrained spring expansion, short 180–260ms feedback. Infinite motion is limited to active listening / processing. Avoid large blur layers and high-frequency pulsing. Respect system animator scale in the follow-up accessibility pass.

## Current implementation boundary
Theme, mark, home, welcome, settings, model/subscription presentation and in-app assistant are native Compose. Login, account recovery, AI chat, TTS, model downloads, cloud/local inference and Google Play Billing are not implemented.
