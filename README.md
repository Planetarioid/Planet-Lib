# Planet Lib

**Shared foundation for the Planet Studios mod ecosystem.**

Planet Lib is a lightweight Fabric library for Minecraft Java Edition **1.21.11**. It provides shared APIs used by Planet Studios mods without forcing unrelated systems to load or run.

## Current development status

- [x] Repository initialized
- [ ] Mod Registry
- [ ] Configuration API
- [ ] Planet Essentials bridge
- [ ] Feature Registry
- [ ] Feature Priority Manager
- [ ] Compatibility API
- [ ] Optional Input API
- [ ] Resource API
- [ ] Diagnostics API
- [ ] Task Utilities
- [ ] Version and Capability Detection

Only completed and tested features will be marked complete.

## First system: Mod Registry

The Mod Registry reads the mods Fabric Loader has successfully loaded and exposes an immutable snapshot of their metadata. Planet Studios mods are identified by the `planet_` mod-ID namespace, including IDs provided as aliases.

The registry reports loaded mods only. It is not a disk scanner, does not inspect arbitrary JAR files, and does not attempt to load or modify other mods.

## Development

- Minecraft: 1.21.11
- Loader: Fabric
- Java: 21
- Build: Gradle + Fabric Loom

Build with `gradle build` and run tests with `gradle test`.

## Design principles

- Keep the library small and modular.
- Add shared functionality only when multiple Planet projects benefit.
- Avoid unnecessary runtime work and dependencies.
- Keep APIs stable, documented, and testable.
- Never silently alter another mod's configuration or behavior.
