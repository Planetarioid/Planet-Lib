package com.planetstudios.planetlib.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable point-in-time view of registered feature metadata.
 */
public final class FeatureRegistrySnapshot {
    private final List<FeatureDescriptor> features;
    private final Map<FeatureId, FeatureDescriptor> byId;

    FeatureRegistrySnapshot(Collection<FeatureDescriptor> descriptors) {
        Objects.requireNonNull(descriptors, "descriptors");

        List<FeatureDescriptor> sorted = new ArrayList<>(descriptors);
        sorted.sort((left, right) -> left.id().compareTo(right.id()));
        this.features = List.copyOf(sorted);

        Map<FeatureId, FeatureDescriptor> index = new LinkedHashMap<>();
        for (FeatureDescriptor descriptor : sorted) {
            index.put(descriptor.id(), descriptor);
        }
        this.byId = Collections.unmodifiableMap(index);
    }

    /** Returns all registered features in deterministic identifier order. */
    public List<FeatureDescriptor> getFeatures() {
        return features;
    }

    /** Finds a feature by its complete mod-namespaced identifier. */
    public Optional<FeatureDescriptor> find(FeatureId id) {
        return Optional.ofNullable(byId.get(Objects.requireNonNull(id, "id")));
    }

    /** Checks whether a feature identifier is present in this snapshot. */
    public boolean isRegistered(FeatureId id) {
        return byId.containsKey(Objects.requireNonNull(id, "id"));
    }

    /** Returns the features owned by a mod, in deterministic identifier order. */
    public List<FeatureDescriptor> getFeaturesForMod(String modId) {
        Objects.requireNonNull(modId, "modId");
        return features.stream()
                .filter(feature -> feature.id().modId().equals(modId))
                .toList();
    }

    /** Returns the number of registered features in this snapshot. */
    public int size() {
        return features.size();
    }
}
