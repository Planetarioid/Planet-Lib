# Compatibility API

The Compatibility API provides a common vocabulary and immutable registry for compatibility declarations made by Planet Studios mods.

## Compatibility levels

- `NATIVE_INTEGRATION`: a direct integration through a supported public API.
- `TESTED_COEXISTENCE`: the exact combination has been exercised in a documented test setup.
- `CONDITIONAL`: coexistence depends on explicitly stated conditions or limitations.
- `DETECT_ONLY`: the mod can be identified, but no behavioral compatibility claim is made.
- `INCOMPATIBLE`: a known incompatibility has been documented.

An absent declaration means unknown. It must never be presented as compatible by default.

## Registration

A declaration identifies the integration mod making the statement, the target mod, its compatibility level, a short summary, and optional conditions. Conditional declarations require conditions. The declaring mod must be present in the Mod Registry; the target may be absent so supported combinations can be documented ahead of installation.

The registry rejects duplicate declarations for the same integration/target pair and publishes immutable, sorted snapshots for lock-free reads.

## Evidence and responsibility

This API stores declarations; it does not independently test, verify, or infer compatibility. Integrations must only use `NATIVE_INTEGRATION` when a real public API integration exists, and `TESTED_COEXISTENCE` only after testing the exact relevant combination. Version-specific checks and capability negotiation are handled by the separate Version and Capability Detection system.

The API does not scan files, load other mods, inject hooks, change settings, or suppress errors. Compatibility claims should include meaningful conditions and be updated when test coverage changes.
