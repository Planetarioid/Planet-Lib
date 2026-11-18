package com.planetstudios.planetlib.capability;

import java.util.Objects;

/** Immutable metadata describing a capability explicitly provided by a mod. */
public record CapabilityDescriptor(CapabilityId id, String description) {
    public CapabilityDescriptor {
        id = Objects.requireNonNull(id, "id");
        description = Objects.requireNonNull(description, "description").trim();
        if (description.isBlank()) {
            throw new IllegalArgumentException("description cannot be blank");
        }
    }
}
