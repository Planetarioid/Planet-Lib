package com.planetstudios.planetlib.resource;

import java.util.Objects;
import java.util.Set;

/** Immutable metadata describing a resource exposed by its owning mod. */
public record ResourceDescriptor(
        ResourceId id,
        String ownerModId,
        ResourceKind kind,
        String description,
        Set<String> tags
) {
    public ResourceDescriptor {
        id = Objects.requireNonNull(id, "id");
        ownerModId = Objects.requireNonNull(ownerModId, "ownerModId");
        if (!ownerModId.matches("[a-z0-9_.-]+")) {
            throw new IllegalArgumentException("ownerModId must be a lowercase mod identifier");
        }
        if (!id.namespace().equals(ownerModId)) {
            throw new IllegalArgumentException("Resource namespace must match its owner mod ID");
        }
        kind = Objects.requireNonNull(kind, "kind");
        description = Objects.requireNonNullElse(description, "");
        tags = tags == null ? Set.of() : Set.copyOf(tags);
        for (String tag : tags) {
            if (tag == null || !tag.matches("[a-z0-9_.-]+")) {
                throw new IllegalArgumentException("Resource tags must be lowercase identifiers");
            }
        }
    }
}
