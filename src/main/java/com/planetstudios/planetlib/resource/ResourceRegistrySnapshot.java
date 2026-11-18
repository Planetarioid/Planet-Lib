package com.planetstudios.planetlib.resource;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Immutable, deterministic view of registered resource metadata. */
public final class ResourceRegistrySnapshot {
    private final List<ResourceDescriptor> resources;
    private final Map<ResourceId, ResourceDescriptor> byId;

    public ResourceRegistrySnapshot(Collection<ResourceDescriptor> resources) {
        this.resources = resources.stream()
                .sorted(Comparator.comparing(ResourceDescriptor::id))
                .toList();
        this.byId = this.resources.stream().collect(Collectors.toUnmodifiableMap(
                ResourceDescriptor::id, Function.identity()));
    }

    public List<ResourceDescriptor> resources() {
        return resources;
    }

    public Optional<ResourceDescriptor> find(ResourceId id) {
        return Optional.ofNullable(byId.get(id));
    }

    public boolean contains(ResourceId id) {
        return byId.containsKey(id);
    }

    public List<ResourceDescriptor> byOwner(String ownerModId) {
        return resources.stream().filter(resource -> resource.ownerModId().equals(ownerModId)).toList();
    }

    public List<ResourceDescriptor> byKind(ResourceKind kind) {
        return resources.stream().filter(resource -> resource.kind() == kind).toList();
    }

    public int size() {
        return resources.size();
    }
}
