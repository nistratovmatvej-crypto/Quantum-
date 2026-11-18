# Quantum visual identity — Alpha 0.2 premium redesign

This document is superseded by [QUANTUM_DESIGN_SYSTEM.md](QUANTUM_DESIGN_SYSTEM.md), the complete Quantum Atlantic design specification.

## Current direction
- Deep ink background: #071522
- Elevated navy: #0B1E2D
- Main surface: #102638
- Raised surface: #163449
- Signature cyan: #079FC0
- Bright cyan: #35BBD5
- Porcelain: #F4F3EF
- Muted text: #91A8B5

The concentric geometric logo replaces the former star identity. The Android mark is drawn natively with Canvas/vector assets.

## Implementation boundary
Native Compose screens use the shared QuantumTheme. The in-app assistant remains in-app; microphone capture is user-triggered. AI models, account authentication, TTS, reminders, device actions and billing are not connected in Alpha 0.2.
