package com.planetstudios.planetlib.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Registers and discovers immutable feature metadata contributed by mods.
 *
 * <p>Registration is intended for mod initialization. Readers access an
 * immutable snapshot, so lookups do not lock or rebuild registry state.</p>
 *
 * <p>This registry deliberately does not control feature activation or resolve
 * conflicts. Those responsibilities belong to the Feature Priority Manager.</p>
 */
public final class FeatureRegistry {
    private final Map<FeatureId, FeatureDescriptor> registered = new HashMap<>();
    private final AtomicReference<FeatureRegistrySnapshot> snapshot =
            new AtomicReference<>(new FeatureRegistrySnapshot(java.util.List.of()));

    /** Creates an isolated registry, useful for tests and independent environments. */
    public FeatureRegistry() {
    }

    /** Returns the process-wide registry used by Planet Studios mods. */
    public static FeatureRegistry getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Registers a feature descriptor.
     *
     * @throws IllegalArgumentException if the identifier has already been registered
     */
    public synchronized FeatureDescriptor register(FeatureDescriptor descriptor) {
        Objects.requireNonNull(descriptor, "descriptor");
        FeatureId id = descriptor.id();
        if (registered.containsKey(id)) {
            throw new IllegalArgumentException("Feature already registered: " + id);
        }

        registered.put(id, descriptor);
        snapshot.set(new FeatureRegistrySnapshot(registered.values()));
        return descriptor;
    }

    /** Returns the latest immutable registry snapshot. */
    public FeatureRegistrySnapshot snapshot() {
        return snapshot.get();
    }

    private static final class Holder {
        private static final FeatureRegistry INSTANCE = new FeatureRegistry();
    }
}
