# Version and Capability Detection

This system provides read-only checks for loaded mod versions and explicit capability declarations.

## Version detection

`ModVersion` parses common numeric version strings with optional prerelease and build metadata. Numeric components are compared numerically; prereleases sort below the corresponding final release. Build metadata does not affect precedence.

`VersionRequirement` supports an inclusive minimum and exclusive maximum. `VersionDetector` checks a supplied immutable Mod Registry snapshot and reports one of: mod not loaded, version unparseable, below minimum, at/above exclusive maximum, or satisfies requirement.

Unrecognized version formats are reported as unparseable. The detector does not guess, normalize arbitrary vendor formats, or assume an unparseable version is compatible.

## Capability declarations

A capability is explicitly advertised by its owning mod with an owner-namespaced `CapabilityId` and description. Registration requires the owner to be present in the Mod Registry. Use `CapabilityRegistry.getInstance()` for the shared registry. Isolated instances can receive an explicit owner-presence predicate for tests. Snapshots are immutable and sorted. A capability is available only when it has been registered and its owner is loaded.

Capabilities are not inferred from mod names, versions, installed files, or compatibility declarations. Absence means unknown/unavailable to this API, not proof that the mod lacks the behavior.

## Boundaries

The system does not scan disk, load mods, inspect private implementation details, invoke optional APIs, or modify runtime behavior. Version checks and capability declarations are separate: satisfying a version range does not prove a capability exists.
