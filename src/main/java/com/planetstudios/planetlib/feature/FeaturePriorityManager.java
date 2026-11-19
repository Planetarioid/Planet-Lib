package com.planetstudios.planetlib.feature;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/** Resolves competing implementations and coordinates provider-owned lifecycle callbacks. */
public final class FeaturePriorityManager {
    private static final Comparator<FeatureImplementation> ORDER =
            Comparator.comparingInt(FeatureImplementation::priority).reversed()
                    .thenComparing(FeatureImplementation::featureId);
    private final FeatureRegistry featureRegistry;
    private final Map<String, Map<FeatureId, FeatureImplementation>> candidates = new HashMap<>();
    private final Map<String, Map<FeatureId, FeatureLifecycle>> lifecycles = new HashMap<>();
    private final Map<String, FeatureId> userPreferences = new HashMap<>();
    private final AtomicReference<FeaturePrioritySnapshot> snapshot =
            new AtomicReference<>(new FeaturePrioritySnapshot(List.of()));

    public static FeaturePriorityManager getInstance() { return Holder.INSTANCE; }
    private static final class Holder {
        private static final FeaturePriorityManager INSTANCE =
                new FeaturePriorityManager(FeatureRegistry.getInstance());
    }
    public FeaturePriorityManager(FeatureRegistry featureRegistry) {
        this.featureRegistry = Objects.requireNonNull(featureRegistry, "featureRegistry");
    }

    /** Metadata-only registration. It participates in resolution without runtime callbacks. */
    public synchronized FeatureImplementation register(FeatureImplementation implementation) {
        return register(implementation, FeatureLifecycle.NONE);
    }

    /** Registers a provider and activates it if it becomes the selected implementation. */
    public synchronized FeatureImplementation register(
            FeatureImplementation implementation, FeatureLifecycle lifecycle) {
        Objects.requireNonNull(implementation, "implementation");
        Objects.requireNonNull(lifecycle, "lifecycle");
        FeatureId id = implementation.featureId();
        String capability = implementation.capabilityId();
        if (!featureRegistry.snapshot().isRegistered(id)) {
            throw new IllegalArgumentException("Feature must be registered first: " + id);
        }
        Map<FeatureId, FeatureImplementation> group =
                candidates.computeIfAbsent(capability, ignored -> new HashMap<>());
        if (group.containsKey(id)) {
            throw new IllegalArgumentException("Feature already implements capability " + capability + ": " + id);
        }

        FeatureId previousSelected = snapshot.get().find(capability)
                .map(resolution -> resolution.selected().featureId()).orElse(null);
        FeatureLifecycle previousLifecycle = previousSelected == null
                ? FeatureLifecycle.NONE : lifecycle(capability, previousSelected);
        group.put(id, implementation);
        lifecycles.computeIfAbsent(capability, ignored -> new HashMap<>()).put(id, lifecycle);
        rebuildSnapshot();
        FeatureId nowSelected = snapshot.get().find(capability).orElseThrow().selected().featureId();

        if (!Objects.equals(previousSelected, nowSelected)) {
            try {
                if (previousSelected != null) previousLifecycle.onDeselected();
                lifecycle(capability, nowSelected).onSelected();
            } catch (RuntimeException failure) {
                group.remove(id);
                Map<FeatureId, FeatureLifecycle> lifecycleGroup = lifecycles.get(capability);
                lifecycleGroup.remove(id);
                if (lifecycleGroup.isEmpty()) lifecycles.remove(capability);
                if (group.isEmpty()) candidates.remove(capability);
                rebuildSnapshot();
                if (previousSelected != null) {
                    try { previousLifecycle.onSelected(); }
                    catch (RuntimeException rollbackFailure) { failure.addSuppressed(rollbackFailure); }
                }
                throw new IllegalStateException("Feature provider transition failed while registering " + id, failure);
            }
        }
        return implementation;
    }

    /** Selects a registered candidate and applies the provider lifecycle transition. */
    public synchronized FeaturePrioritySnapshot preferImplementation(String capabilityId, FeatureId featureId) {
        Objects.requireNonNull(capabilityId, "capabilityId");
        Objects.requireNonNull(featureId, "featureId");
        Map<FeatureId, FeatureImplementation> group = candidates.get(capabilityId);
        if (group == null || !group.containsKey(featureId)) {
            throw new IllegalArgumentException("Feature is not a registered implementation of "
                    + capabilityId + ": " + featureId);
        }
        FeatureId previousPreference = userPreferences.get(capabilityId);
        FeatureId previousSelected = snapshot.get().find(capabilityId).orElseThrow().selected().featureId();
        if (previousSelected.equals(featureId)) {
            userPreferences.put(capabilityId, featureId);
            rebuildSnapshot();
            return snapshot.get();
        }
        transition(capabilityId, previousSelected, featureId,
                () -> userPreferences.put(capabilityId, featureId),
                () -> restorePreference(capabilityId, previousPreference));
        return snapshot.get();
    }

    /** Clears an explicit preference and restores the highest-priority provider. */
    public synchronized FeaturePrioritySnapshot clearPreference(String capabilityId) {
        Objects.requireNonNull(capabilityId, "capabilityId");
        FeatureResolution current = snapshot.get().find(capabilityId).orElse(null);
        if (current == null) {
            userPreferences.remove(capabilityId);
            return snapshot.get();
        }
        FeatureId previousPreference = userPreferences.get(capabilityId);
        FeatureId automatic = current.implementations().stream().sorted(ORDER).findFirst()
                .orElseThrow().featureId();
        if (current.selected().featureId().equals(automatic)) {
            userPreferences.remove(capabilityId);
            rebuildSnapshot();
            return snapshot.get();
        }
        transition(capabilityId, current.selected().featureId(), automatic,
                () -> userPreferences.remove(capabilityId),
                () -> restorePreference(capabilityId, previousPreference));
        return snapshot.get();
    }

    public FeaturePrioritySnapshot snapshot() { return snapshot.get(); }
    public Optional<FeatureResolution> findResolution(String capabilityId) { return snapshot().find(capabilityId); }

    private void transition(String capability, FeatureId from, FeatureId to,
                            Runnable applyPreference, Runnable rollbackPreference) {
        FeatureLifecycle oldLifecycle = lifecycle(capability, from);
        FeatureLifecycle newLifecycle = lifecycle(capability, to);
        try {
            oldLifecycle.onDeselected();
        } catch (RuntimeException failure) {
            throw new IllegalStateException("Feature provider failed to deactivate: " + from, failure);
        }
        applyPreference.run();
        rebuildSnapshot();
        try {
            newLifecycle.onSelected();
        } catch (RuntimeException activationFailure) {
            rollbackPreference.run();
            rebuildSnapshot();
            try { oldLifecycle.onSelected(); }
            catch (RuntimeException rollbackFailure) { activationFailure.addSuppressed(rollbackFailure); }
            throw new IllegalStateException("Feature provider failed to activate: " + to
                    + "; previous selection was restored where possible", activationFailure);
        }
    }

    private FeatureLifecycle lifecycle(String capability, FeatureId id) {
        return lifecycles.getOrDefault(capability, Map.of()).getOrDefault(id, FeatureLifecycle.NONE);
    }
    private void restorePreference(String capability, FeatureId previous) {
        if (previous == null) userPreferences.remove(capability);
        else userPreferences.put(capability, previous);
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
