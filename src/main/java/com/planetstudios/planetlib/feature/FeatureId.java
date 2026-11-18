package com.planetstudios.planetlib.feature;

import java.util.Objects;

/**
 * Stable identifier for a feature, namespaced by the mod that owns it.
 */
public record FeatureId(String modId, String featureId) implements Comparable<FeatureId> {
    public FeatureId {
        validate(modId, "modId");
        validate(featureId, "featureId");
    }

    private static void validate(String value, String name) {
        Objects.requireNonNull(value, name);
        if (!value.matches("[a-z0-9_.-]+") || value.equals(".") || value.equals("..")) {
            throw new IllegalArgumentException(name
                    + " must contain only lowercase letters, digits, '.', '_' or '-'");
        }
    }

    @Override
    public int compareTo(FeatureId other) {
        int modComparison = modId.compareTo(other.modId);
        return modComparison != 0 ? modComparison : featureId.compareTo(other.featureId);
    }

    @Override
    public String toString() {
        return modId + ":" + featureId;
    }
}
