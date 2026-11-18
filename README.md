# Planet Lib

**Shared foundation for the Planet Studios mod ecosystem.**

Planet Lib is a lightweight Fabric library for Minecraft Java Edition **1.21.11**. It provides shared APIs used by Planet Studios mods without forcing unrelated systems to load or run.

## Current development status

- [x] Repository initialized
- [x] Mod Registry
- [x] Configuration API
- [x] Feature Registry
- [x] Feature Priority Manager
- [ ] Planet Essentials bridge
- [ ] Compatibility API
- [ ] Version and Capability Detection
- [ ] Diagnostics API
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
