# Quantum

**Quantum Alpha 0.1** — native Android voice-first AI assistant.

Quantum combines conversational AI with consent-based Android device control. Its floating star is manually activated: tapping the star expands the compact control into a horizontal panel; tapping the panel starts microphone capture. After 15 seconds without detected speech, capture stops and the panel collapses.

## Product principles

- Native Android application, built with Kotlin.
- Hybrid intelligence: cloud AI for advanced requests, local AI for essential offline capabilities.
- Device actions require user confirmation before execution.
- No always-on microphone or passive wake-word listening.
- Device-aware local model tiers: Meteor, Comet, Nova.
- User-selectable memory modes with clear controls.
- Voice library with individually downloadable voices and a multi-select download queue.
- Free core experience; optional Quantum Supernova Pro subscription adds higher cloud limits and additional features.

## Current stage

This repository starts at **Quantum Alpha 0.1**. All pre-release builds use the Quantum Alpha 0.x naming sequence. The global release will receive its own release version.

## Documentation

- [Product specification](docs/PRODUCT_SPEC.md)
- [Technical architecture](docs/ARCHITECTURE.md)
- [Development roadmap](docs/ROADMAP.md)

## Status

Planning and foundation. No production AI provider, model weights, licensed character voices, or device-control implementation are bundled yet.
