package com.planetstudios.planetlib.capability;

import com.planetstudios.planetlib.registry.ModRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/** Registry of capabilities explicitly advertised by loaded mods. */
public final class CapabilityRegistry {
    private final Predicate<String> isModLoaded;
    private final Map<CapabilityId, CapabilityDescriptor> capabilities = new HashMap<>();
    private final AtomicReference<CapabilitySnapshot> snapshot =
            new AtomicReference<>(new CapabilitySnapshot(List.of()));

    /** Creates a registry using the current Fabric-loaded mod snapshot. */
    public CapabilityRegistry() {
        this(modId -> ModRegistry.snapshot().isLoaded(modId));
    }

    /** Creates an isolated registry with an explicit owner-presence check. */
    public CapabilityRegistry(Predicate<String> isModLoaded) {
        this.isModLoaded = Objects.requireNonNull(isModLoaded, "isModLoaded");
    }

    /** Returns the shared process-wide capability registry. */
    public static CapabilityRegistry getInstance() {
        return Holder.INSTANCE;
    }

    public synchronized CapabilityDescriptor register(CapabilityDescriptor descriptor) {
        Objects.requireNonNull(descriptor, "descriptor");
        if (!isModLoaded.test(descriptor.id().ownerModId())) {
            throw new IllegalArgumentException("Capability owner is not loaded: " + descriptor.id().ownerModId());
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

    private static final class Holder {
        private static final CapabilityRegistry INSTANCE = new CapabilityRegistry();
    }
}
