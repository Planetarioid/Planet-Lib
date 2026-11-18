package com.planetstudios.planetlib.feature;

import java.util.Objects;
import java.util.Set;

/**
 * Immutable descriptive metadata for a feature registered by a mod.
 *
 * <p>A descriptor does not enable, disable, schedule, or otherwise execute
 * the feature. Those responsibilities belong to higher-level systems.</p>
 */
public record FeatureDescriptor(
        FeatureId id,
        String displayName,
        String description,
        String category,
        Set<String> tags
) {
    public FeatureDescriptor {
        id = Objects.requireNonNull(id, "id");
        displayName = Objects.requireNonNull(displayName, "displayName").trim();
        if (displayName.isEmpty()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }
        description = Objects.requireNonNullElse(description, "");
        category = Objects.requireNonNullElse(category, "general").trim();
        if (category.isEmpty() || !category.matches("[a-z0-9_.-]+")) {
            throw new IllegalArgumentException("category must be a non-empty lowercase identifier");
        }
        tags = tags == null ? Set.of() : Set.copyOf(tags);
        for (String tag : tags) {
            if (tag == null || !tag.matches("[a-z0-9_.-]+")) {
                throw new IllegalArgumentException("Feature tags must be non-null lowercase identifiers");
            }
        }
    }
}
