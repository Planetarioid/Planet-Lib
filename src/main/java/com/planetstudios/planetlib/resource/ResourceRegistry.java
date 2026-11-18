package com.planetstudios.planetlib.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Registry of logical resource metadata contributed by loaded mods.
 *
 * <p>This API does not load files, scan resource packs, access arbitrary paths,
 * or perform rendering/audio work. Consumers resolve actual assets through
 * Minecraft/Fabric or their owning mod's asset system.</p>
 */
public final class ResourceRegistry {
    private final Map<ResourceId, ResourceDescriptor> registered = new HashMap<>();
    private final AtomicReference<ResourceRegistrySnapshot> snapshot =
            new AtomicReference<>(new ResourceRegistrySnapshot(java.util.List.of()));

    public ResourceRegistry() {
    }

    public static ResourceRegistry getInstance() {
        return Holder.INSTANCE;
    }

    public synchronized ResourceDescriptor register(ResourceDescriptor descriptor) {
        Objects.requireNonNull(descriptor, "descriptor");
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
