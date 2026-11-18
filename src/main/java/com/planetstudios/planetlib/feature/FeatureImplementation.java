package com.planetstudios.planetlib.feature;

import java.util.Objects;

/** Declares that a registered feature provides a shared logical capability. */
public record FeatureImplementation(String capabilityId, FeatureId featureId, int priority) {
    public FeatureImplementation {
        capabilityId = Objects.requireNonNull(capabilityId, "capabilityId").trim();
        featureId = Objects.requireNonNull(featureId, "featureId");
        if (!capabilityId.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")
                || capabilityId.startsWith(".:") || capabilityId.startsWith("..:")) {
            throw new IllegalArgumentException("capabilityId must use lowercase namespace:path format");
        }
        for (String segment : capabilityId.substring(capabilityId.indexOf(':') + 1).split("/")) {
            if (segment.isEmpty() || segment.equals(".") || segment.equals("..")) {
                throw new IllegalArgumentException("capabilityId contains an invalid path segment");
            }
        }
    }
}
