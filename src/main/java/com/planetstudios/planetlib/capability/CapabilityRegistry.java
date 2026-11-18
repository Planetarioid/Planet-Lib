package com.planetstudios.planetlib.capability;

import com.planetstudios.planetlib.registry.ModRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Registry of capabilities explicitly advertised by loaded mods.
 * It never infers capabilities from a mod ID or version.
 */
public final class CapabilityRegistry {
    private final Map<CapabilityId, CapabilityDescriptor> capabilities = new HashMap<>();
    private final AtomicReference<CapabilitySnapshot> snapshot =
            new AtomicReference<>(new CapabilitySnapshot(List.of()));

    public synchronized CapabilityDescriptor register(CapabilityDescriptor descriptor) {
        Objects.requireNonNull(descriptor, "descriptor");
        if (!ModRegistry.snapshot().isLoaded(descriptor.id().ownerModId())) {
            throw new IllegalArgumentException("Capability owner is not loaded: "
                    + descriptor.id().ownerModId());
        }
        if (capabilities.putIfAbsent(descriptor.id(), descriptor) != null) {
            throw new IllegalArgumentException("Capability already registered: " + descriptor.id());
        }
        snapshot.set(new CapabilitySnapshot(new ArrayList<>(capabilities.values())));
        return descriptor;
    }

    public CapabilitySnapshot snapshot() {
        return snapshot.get();
    }
}
