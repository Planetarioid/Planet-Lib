package com.planetstudios.planetlib.feature;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Immutable resolution for all implementations of one shared capability. */
public final class FeatureResolution {
    private final String capabilityId;
    private final List<FeatureImplementation> implementations;
    private final FeatureImplementation selected;
    private final boolean userPreferred;

    FeatureResolution(String capabilityId, List<FeatureImplementation> implementations,
                      FeatureImplementation selected, boolean userPreferred) {
        this.capabilityId = Objects.requireNonNull(capabilityId, "capabilityId");
        this.implementations = List.copyOf(implementations);
        this.selected = Objects.requireNonNull(selected, "selected");
        this.userPreferred = userPreferred;
    }

    public String capabilityId() { return capabilityId; }
    public List<FeatureImplementation> implementations() { return implementations; }
    public FeatureImplementation selected() { return selected; }
    public boolean isUserPreferred() { return userPreferred; }

    public Optional<FeatureImplementation> findImplementation(FeatureId featureId) {
        Objects.requireNonNull(featureId, "featureId");
        return implementations.stream().filter(candidate -> candidate.featureId().equals(featureId)).findFirst();
    }
}
