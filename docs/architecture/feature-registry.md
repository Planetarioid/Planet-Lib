# Feature Registry

## Purpose

The Feature Registry provides a shared, read-only discovery layer for individual features contributed by Planet Studios mods. It gives each feature a stable identifier and immutable descriptive metadata.

The registry is intentionally separate from feature activation, scheduling, and conflict resolution. Those responsibilities belong to higher-level systems, especially the Feature Priority Manager.

## Identifiers

Every feature is identified by a pair:

- Owning mod ID
- Feature ID within that mod

For example, `planet_projectile_optimizations:arrow-rendering`.

This allows different mods to use the same short feature ID without colliding. Both identifier segments use lowercase letters, digits, periods, underscores, and hyphens.

## Metadata

A `FeatureDescriptor` contains:

- Namespaced `FeatureId`
- Human-readable display name
- Optional description
- Category identifier
- Immutable tags

Descriptors are immutable. The registry does not infer behavior from display names, descriptions, categories, or tags.

## API

- `FeatureRegistry.getInstance()` returns the shared process-wide registry.
- `FeatureRegistry.register(descriptor)` registers a feature during mod initialization.
- `FeatureRegistry.snapshot()` returns the latest immutable snapshot.
- `FeatureRegistrySnapshot.getFeatures()` returns all features in deterministic ID order.
- `FeatureRegistrySnapshot.find(id)` looks up a feature.
- `FeatureRegistrySnapshot.isRegistered(id)` checks registration.
- `FeatureRegistrySnapshot.getFeaturesForMod(modId)` returns features owned by a mod.
- `FeatureRegistrySnapshot.size()` returns the number of registered features.

A standalone `FeatureRegistry` instance can also be created for isolated tests.

## Guarantees and limits

- Registration rejects duplicate namespaced identifiers.
- Different mods may register the same feature ID.
- Snapshots and descriptor metadata are immutable.
- Snapshot publication is atomic.
- Registration is synchronized; snapshot reads are lock-free.
- Registration is intended for mod initialization, not tick loops.
- No feature is enabled, disabled, prioritized, or executed by this registry.
- No conflicts are resolved here.
- The registry does not inspect arbitrary mods or infer feature support.

## Next integration

The Feature Priority Manager will consume registered feature descriptors and provide explicit coordination for features that overlap. The registry itself remains a neutral metadata and discovery service.
