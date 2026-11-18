package com.planetstudios.planetlib.resource;

import com.planetstudios.planetlib.registry.ModRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * Registry of logical resource metadata contributed by loaded mods.
 * This API does not load files, scan resource packs, access arbitrary paths,
 * or perform rendering/audio work.
 */
public final class ResourceRegistry {
    private final Predicate<String> isModLoaded;
    private final Map<ResourceId, ResourceDescriptor> registered = new HashMap<>();
    private final AtomicReference<ResourceRegistrySnapshot> snapshot =
            new AtomicReference<>(new ResourceRegistrySnapshot(java.util.List.of()));

    /** Creates a registry using the current Fabric-loaded mod snapshot. */
    public ResourceRegistry() {
        this(modId -> ModRegistry.snapshot().isLoaded(modId));
    }

    /** Creates an isolated registry with an explicit owner-presence check. */
    public ResourceRegistry(Predicate<String> isModLoaded) {
        this.isModLoaded = Objects.requireNonNull(isModLoaded, "isModLoaded");
    }

    public static ResourceRegistry getInstance() {
        return Holder.INSTANCE;
    }

    public synchronized ResourceDescriptor register(ResourceDescriptor descriptor) {
        Objects.requireNonNull(descriptor, "descriptor");
        if (!isModLoaded.test(descriptor.ownerModId())) {
            throw new IllegalArgumentException("Resource owner is not loaded: " + descriptor.ownerModId());
        }
        if (registered.containsKey(descriptor.id())) {
            throw new IllegalArgumentException("Resource already registered: " + descriptor.id());
        }
        registered.put(descriptor.id(), descriptor);
        snapshot.set(new ResourceRegistrySnapshot(registered.values()));
        return descriptor;
    }

    public ResourceRegistrySnapshot snapshot() {
        return snapshot.get();
    }

    private static final class Holder {
        private static final ResourceRegistry INSTANCE = new ResourceRegistry();
    }
}
