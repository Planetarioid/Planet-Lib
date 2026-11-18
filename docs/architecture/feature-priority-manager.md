# Feature Priority Manager

The Feature Priority Manager resolves multiple implementations that advertise the same logical capability.

## Identity model

A FeatureId identifies a feature owned by one mod, such as planet_projectile_optimizations:arrow-rendering. A capabilityId identifies the shared behavior those features can provide, such as planet:rendering/entity. Multiple mods can register different features against the same capability.

Capability identifiers use lowercase namespace:path syntax. Providers register a FeatureImplementation with an integer priority. Higher values win automatic selection. Equal priorities are resolved by ascending FeatureId, making the result deterministic regardless of registration order.

## User preference

A user or higher-level configuration UI can call preferImplementation(capabilityId, featureId) to explicitly choose one registered provider. The manager rejects preferences that do not match a registered candidate. clearPreference(capabilityId) removes the override and restores automatic selection.

Preferences are scoped to one capability. Changing the implementation for one capability does not disable its provider mod or affect unrelated capabilities. Persistence and UI belong to the configuration/Essentials integration layer, not this in-memory API.

## Safety and responsibility boundaries

The manager publishes immutable snapshots and performs no per-tick work. It does not invoke provider code, install hooks, enable or disable mods, edit another mod's settings, or automatically activate/deactivate runtime systems. Integrating mods must consult the selected implementation and apply the choice through their own supported lifecycle.

The manager does not silently change an explicit user preference. If provider-unregistration is added later, stale preferences must be surfaced and resolved deliberately rather than silently selecting a different provider.
