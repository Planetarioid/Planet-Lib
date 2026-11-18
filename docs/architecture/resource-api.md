# Resource API

## Purpose

The Resource API provides a lightweight, shared index of logical resources contributed by Planet Studios mods. It gives integrations stable namespaced IDs, descriptive metadata, duplicate detection, and immutable lookup snapshots.

## Components

- `ResourceId`: validated `namespace:path` identifier. The namespace must match the owning mod ID.
- `ResourceKind`: broad category (texture, model, audio, shader, data, or other).
- `ResourceDescriptor`: immutable owner, category, description, and tags.
- `ResourceRegistry`: synchronized initialization-time registration with lock-free snapshot reads.
- `ResourceRegistrySnapshot`: deterministic lookup by ID, owner, or kind.

## Boundaries

This is metadata registration, not an asset pipeline. It does not read files, scan resource packs, load arbitrary paths or URLs, decode assets, render textures, play audio, or modify resource-pack contents. Minecraft/Fabric and the relevant owning mod remain responsible for actual asset resolution and loading. Planet Essentials and Planet Advanced Textures & Audio retain their dedicated asset-management responsibilities.

Registration rejects duplicate IDs and namespace/owner mismatches. Snapshots are immutable and remain stable after later registrations. Register during mod initialization, not on a per-tick path.
