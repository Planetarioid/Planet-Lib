# Mod Registry

## Purpose

The Mod Registry is Planet Lib's read-only view of mods that Fabric Loader has successfully loaded. Fabric Loader remains responsible for discovering, resolving, and loading mods.

## Public API

- `ModRegistry.snapshot()` returns the latest immutable snapshot.
- `ModRegistry.refresh()` rebuilds and atomically publishes a snapshot. Call during initialization or explicit diagnostics, never from a tick loop.
- `ModRegistrySnapshot.getMods()` returns all loaded mod descriptors.
- `ModRegistrySnapshot.getPlanetMods()` returns descriptors identified as Planet Studios projects.
- `ModRegistrySnapshot.find(id)` looks up a canonical mod ID.
- `ModRegistrySnapshot.isLoaded(id)` checks whether a canonical ID is present.

Each `ModDescriptor` contains the canonical ID, display name, version, description, loader type, provided aliases, optional nested-mod parent ID, and Planet Studios classification.

## First-party identification

A mod is classified as a Planet Studios mod when its canonical ID or one of its declared provided IDs begins with `planet_`. Display names are deliberately ignored because they are not unique or authoritative.

## Guarantees and limits

- Snapshots and descriptor collections are immutable.
- Snapshot replacement is atomic; readers never observe a partially rebuilt registry.
- Entries are sorted by canonical mod ID for deterministic presentation.
- Only Fabric Loader's loaded-mod collection is read.
- The registry does not scan the mods directory, parse arbitrary JARs, load mods, edit metadata, or infer compatibility.
- The registry is a point-in-time view. If a future loader integration supports dynamic mod changes, the caller must explicitly refresh.

## Performance

Refresh is intended for startup or deliberate diagnostics. Read operations use the already-built snapshot and do not query Fabric Loader repeatedly.
