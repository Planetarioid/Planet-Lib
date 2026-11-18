# Configuration API

## Purpose

The Configuration API gives each mod a typed, JSON-backed configuration file through a small shared interface. Planet Lib owns file handling; each mod owns its configuration class and defaults.

## Registration

Use the shared manager from a mod initializer:

```java
ConfigManager.ConfigHandle<ClientConfig> config =
        ConfigManager.getInstance().register(
                "example_mod",
                "client",
                ClientConfig.class,
                ClientConfig::new
        );
```

The file is stored at `config/planetlib/<modId>/<fileName>.json`. Mod IDs and file names are restricted to safe lowercase identifier characters. Each registration key can be registered only once per manager.

## Handle operations

- `get()` returns the current in-memory object.
- `set(value)` replaces the in-memory object.
- `save()` explicitly persists the current object.
- `reload()` explicitly reloads from disk.
- `getPath()` reports the file location.

The API does not poll files, watch directories, or perform work per tick. Callers are responsible for coordinating access to mutable configuration objects returned by `get()`.

## File safety

- First registration writes defaults only when no file exists.
- Existing malformed JSON causes an `IOException`; the original file is not overwritten.
- Writes use a temporary file in the same directory and atomic replacement where the filesystem supports it.
- If atomic moves are unsupported, the API falls back to a normal replacement move.
- A removed file is recreated from defaults only when the caller explicitly invokes `reload()`.

## Boundaries

This is the foundational storage API, not a graphical settings screen and not a remote configuration system. It does not edit another mod's settings, synchronize values across clients and servers, or impose a universal schema. A future Planet Essentials bridge can build a settings interface on top of explicit mod-owned configuration registrations.
