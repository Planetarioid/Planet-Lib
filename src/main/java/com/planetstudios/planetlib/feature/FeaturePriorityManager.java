package com.planetstudios.planetlib.feature;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Resolves competing implementations of shared capabilities.
 * It does not enable/disable mods, invoke provider code, or install runtime hooks.
 */
public final class FeaturePriorityManager {
    private static final Comparator<FeatureImplementation> ORDER =
            Comparator.comparingInt(FeatureImplementation::priority).reversed()
                    .thenComparing(FeatureImplementation::featureId);

    private final FeatureRegistry featureRegistry;

    /** Returns the process-wide manager used by installed Planet mods and Essentials. */
    public static FeaturePriorityManager getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final FeaturePriorityManager INSTANCE =
                new FeaturePriorityManager(FeatureRegistry.getInstance());
    }
    private final Map<String, Map<FeatureId, FeatureImplementation>> candidates = new HashMap<>();
    private final Map<String, FeatureId> userPreferences = new HashMap<>();
    private final AtomicReference<FeaturePrioritySnapshot> snapshot =
            new AtomicReference<>(new FeaturePrioritySnapshot(List.of()));

    public FeaturePriorityManager(FeatureRegistry featureRegistry) {
        this.featureRegistry = Objects.requireNonNull(featureRegistry, "featureRegistry");
    }

    /** Registers an implementation after its feature metadata has been registered. */
    public synchronized FeatureImplementation register(FeatureImplementation implementation) {
        Objects.requireNonNull(implementation, "implementation");
        if (!featureRegistry.snapshot().isRegistered(implementation.featureId())) {
            throw new IllegalArgumentException("Feature must be registered first: " + implementation.featureId());
        }
        Map<FeatureId, FeatureImplementation> group =
                candidates.computeIfAbsent(implementation.capabilityId(), ignored -> new HashMap<>());
        if (group.putIfAbsent(implementation.featureId(), implementation) != null) {
            throw new IllegalArgumentException("Feature already implements capability "
                    + implementation.capabilityId() + ": " + implementation.featureId());
        }
        rebuildSnapshot();
        return implementation;
    }

    /** Explicitly selects one candidate for a capability. */
    public synchronized FeaturePrioritySnapshot preferImplementation(String capabilityId, FeatureId featureId) {
        Objects.requireNonNull(capabilityId, "capabilityId");
        Objects.requireNonNull(featureId, "featureId");
        Map<FeatureId, FeatureImplementation> group = candidates.get(capabilityId);
        if (group == null || !group.containsKey(featureId)) {
            throw new IllegalArgumentException("Feature is not a registered implementation of "
                    + capabilityId + ": " + featureId);
        }
        userPreferences.put(capabilityId, featureId);
        rebuildSnapshot();
        return snapshot.get();
    }

    /** Removes the explicit override and restores automatic priority selection. */
    public synchronized FeaturePrioritySnapshot clearPreference(String capabilityId) {
        Objects.requireNonNull(capabilityId, "capabilityId");
        userPreferences.remove(capabilityId);
        rebuildSnapshot();
        return snapshot.get();
    }

    public FeaturePrioritySnapshot snapshot() { return snapshot.get(); }

    public Optional<FeatureResolution> findResolution(String capabilityId) {
        return snapshot().find(capabilityId);
    }

    private void rebuildSnapshot() {
        List<FeatureResolution> resolutions = new ArrayList<>();
        for (Map.Entry<String, Map<FeatureId, FeatureImplementation>> entry : candidates.entrySet()) {
            List<FeatureImplementation> ordered = new ArrayList<>(entry.getValue().values());
            ordered.sort(ORDER);
            FeatureId preferredId = userPreferences.get(entry.getKey());
            FeatureImplementation selected = preferredId == null
                    ? ordered.get(0) : entry.getValue().get(preferredId);
            resolutions.add(new FeatureResolution(entry.getKey(), ordered, selected, preferredId != null));
        }
        snapshot.set(new FeaturePrioritySnapshot(resolutions));
    }
}
