package com.planetstudios.planetlib.registry;

import java.util.Objects;
import java.util.Set;

/**
 * Immutable, loader-independent view of a loaded mod's public metadata.
 */
public record ModDescriptor(
        String id,
        String name,
        String version,
        String description,
        String type,
        Set<String> providedIds,
        String parentModId,
        boolean planetMod
) {
    public ModDescriptor {
        id = Objects.requireNonNull(id, "id");
        name = Objects.requireNonNullElse(name, id);
        version = Objects.requireNonNullElse(version, "unknown");
        description = Objects.requireNonNullElse(description, "");
        type = Objects.requireNonNullElse(type, "unknown");
        providedIds = providedIds == null ? Set.of() : Set.copyOf(providedIds);
    }
}
