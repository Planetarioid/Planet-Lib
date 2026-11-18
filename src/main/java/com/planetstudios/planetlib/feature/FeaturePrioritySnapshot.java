package com.planetstudios.planetlib.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Immutable point-in-time view of capability implementation selections. */
public final class FeaturePrioritySnapshot {
    private final List<FeatureResolution> resolutions;
    private final Map<String, FeatureResolution> byCapability;

    FeaturePrioritySnapshot(Collection<FeatureResolution> resolutions) {
        Objects.requireNonNull(resolutions, "resolutions");
        List<FeatureResolution> sorted = new ArrayList<>(resolutions);
        sorted.sort((left, right) -> left.capabilityId().compareTo(right.capabilityId()));
        this.resolutions = List.copyOf(sorted);
        Map<String, FeatureResolution> index = new LinkedHashMap<>();
        for (FeatureResolution resolution : sorted) index.put(resolution.capabilityId(), resolution);
        this.byCapability = Collections.unmodifiableMap(index);
    }

    public List<FeatureResolution> getResolutions() { return resolutions; }

    public Optional<FeatureResolution> find(String capabilityId) {
        return Optional.ofNullable(byCapability.get(Objects.requireNonNull(capabilityId, "capabilityId")));
    }

    public int size() { return resolutions.size(); }
}
