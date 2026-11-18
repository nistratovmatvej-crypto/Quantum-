# Quantum Fluid Design System — Alpha 0.3

## Direction
Original Quantum identity informed by the material depth and continuity principles demonstrated in Apple's Liquid Glass / Live Activities. This is not an Apple UI clone.

## Tokens
- Background: #071522
- Elevated background: #0B1E2D
- Surface: #102638
- Raised surface: #163449
- Accent cyan: #079FC0
- Accent bright: #35BBD5
- Porcelain: #F4F3EF
- Muted text: #91A8B5
- Outline: #244354

## Geometry
- Island compact: 132 × 46 dp
- Island expanded: 222 × 58 dp
- Capsule radius: 50% of height
- Main surfaces: 24–34 dp corner radii
- Use one-pixel low-contrast outlines sparingly.

## Motion
- Island morph: 240 ms size interpolation
- Touch press: scale 0.97 over 90 ms; recover over 150 ms
- Background state: 62% opacity, y=2 dp, compact dimensions
- Foreground state: 100% opacity, y=8 dp
- Avoid continuous high-cost blur and large particle effects.

## Components
- Geometric concentric Quantum mark, cyan edge and porcelain core
- Compact overlay with mark only
- Expanded overlay with Quantum label and affordance
- In-app assistant panel retains the existing PanelController state machine
- Microphone remains a deliberate user action; no wake-word or always-listening behavior in this release.

## Accessibility and privacy
- Overlay is opt-in and requires Android's special overlay permission.
- Foreground service notification remains visible while the system island is active.
- Long-press the island to dismiss it.
- Microphone permission is requested in the app, not silently by the overlay.
