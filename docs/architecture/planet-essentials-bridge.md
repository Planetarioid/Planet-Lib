# Planet Essentials Bridge

The bridge connects Planet Lib to Planet Essentials when Essentials is installed, without adding a hard dependency.

## Discovery contract

Planet Essentials exposes exactly one Fabric entrypoint under the key `planet_essentials_api`. That entrypoint implements `PlanetEssentialsApi` and reports its API version. The initial contract version is 1.

Planet Lib checks whether the `planet_essentials` mod is loaded, retrieves the typed entrypoint, and accepts only the supported API version. Missing, duplicate, incompatible, or failing entrypoints produce a reported bridge status. They do not prevent Planet Lib from initializing.

## Integration registration

A Planet mod can describe itself with immutable `PlanetIntegration` metadata: its mod ID, display name, and supported capability identifiers. It can then pass that descriptor to `PlanetEssentialsBridge.registerIntegration`. Registration returns false when Essentials is unavailable and true when the API registration call completes.

This first bridge milestone deliberately establishes discovery, version negotiation, and integration metadata only. It does not expose or mutate another mod's configuration, add settings UI, or automatically enable/disable features. Those require a separately designed, user-controlled settings contract.

## Lifecycle and safety

Discovery is explicit and intended for mod initialization, never per tick or per render. The bridge uses Fabric's typed entrypoint mechanism, not reflection. It references no client-only Minecraft classes and is safe to query from common initialization code.

Planet Lib remains fully usable when Essentials is absent. An unavailable bridge is a normal state, not a startup error.
