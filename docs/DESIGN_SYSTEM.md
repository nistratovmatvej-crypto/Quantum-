# Quantum visual identity — Alpha 0.1.2 design direction

## Brand mark
The supplied concentric-circle symbol is the basis of the Quantum mark. The app uses a native vector/Canvas implementation so it remains sharp at different sizes and does not depend on a remote image.

## Palette
- Atlantic background: `#071B22`
- Elevated background: `#0B252E`
- Main surface: `#102F39`
- Raised surface: `#173B46`
- Signature cyan: `#08A8C9`
- Bright cyan: `#36C9E5`
- Warm porcelain: `#F4F3EF`
- Muted text: `#A4BAC0`

## Interface principles
- Native Material 3 components and Android accessibility semantics.
- iOS-inspired hierarchy: generous spacing, clear type scale, restrained rounded cards, quiet controls and focused primary content.
- Do not reproduce Apple's proprietary UI, icons, fonts, or branding.
- Keep the assistant activation control visible and easy to reach.
- Do not imply unavailable capabilities are active. Alpha status labels remain explicit.
- Avoid gratuitous gradients, neon effects, excessive glass blur, and dense dashboards.

## Current scope
This change updates the theme, home screen, settings screen, in-app mark and adaptive launcher icon. It is a visual refresh only: microphone capture, speech recognition, AI providers, local model inference and device actions remain outside this change.
