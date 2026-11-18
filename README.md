# Planet Lib

**Shared foundation for the Planet Studios mod ecosystem.**

Planet Lib is a lightweight Fabric library for Minecraft Java Edition **1.21.11**. It provides shared APIs used by Planet Studios mods without forcing unrelated systems to load or run.

## Current development status

- [x] Repository initialized
- [x] Mod Registry
- [x] Configuration API
- [x] Feature Registry
- [x] Feature Priority Manager
- [x] Planet Essentials bridge
- [x] Compatibility API
- [x] Version and Capability Detection
- [ ] Diagnostics API (implementation committed; CI verification pending)
- [ ] Resource API
- [ ] Task Utilities
- [ ] Optional Input API

Only completed and tested features will be marked complete.

## Implemented systems

### Mod Registry

Reads the mods Fabric Loader has successfully loaded and exposes an immutable snapshot of their metadata. Planet Studios mods are identified by the planet_ mod-ID namespace, including IDs provided as aliases. It does not scan disk or inspect arbitrary JAR files.

### Configuration API

Provides typed configuration handles, isolated per-mod configuration files, explicit save/reload operations, atomic replacement where supported, and protection against malformed files and unsafe paths.

### Feature Registry

Registers immutable, mod-namespaced feature metadata and exposes deterministic snapshots for discovery. It does not control feature activation or resolve conflicts.

### Feature Priority Manager

Groups implementations by shared capability, selects the highest-priority provider deterministically, and supports explicit reversible user preferences. Selection is scoped per capability. The manager does not disable mods, invoke provider code, or install runtime hooks. See docs/architecture/feature-priority-manager.md.

### Planet Essentials Bridge

Discovers the optional Planet Essentials Fabric entrypoint, negotiates API version 1, and accepts immutable integration metadata. Planet Essentials is not a required dependency. The first milestone does not modify other mods’ settings or provide settings UI. See docs/architecture/planet-essentials-bridge.md.

### Compatibility API

Provides explicit compatibility declarations with five evidence levels: native integration, tested coexistence, conditional, detect-only, and known incompatible. Missing declarations remain unknown. Immutable snapshots are available to integrations; the API does not independently verify compatibility or alter other mods. See docs/architecture/compatibility-api.md.

### Version and Capability Detection

Parses common mod version formats, checks inclusive-minimum/exclusive-maximum requirements against loaded-mod snapshots, and supports explicit owner-namespaced capability declarations. Version checks and capability availability remain separate; unknown versions and undeclared capabilities are never assumed available. See docs/architecture/version-capability-detection.md.

### Diagnostics API

Provides a bounded, thread-safe in-memory event recorder with severity levels, immutable snapshots, eviction counts, and optional caller-measured durations. Recording is explicit; it does not poll game state, write files, upload telemetry, or automatically collect personal data. See docs/architecture/diagnostics-api.md.

## Development

- Minecraft: 1.21.11
- Loader: Fabric
- Java: 21
- Build: Gradle + Fabric Loom

Build with gradle build and run tests with gradle test.

## Design principles

- Keep the library small and modular.
- Add shared functionality only when multiple Planet projects benefit.
- Avoid unnecessary runtime work and dependencies.
- Keep APIs stable, documented, and testable.
- Never silently alter another mod's configuration or behavior.
