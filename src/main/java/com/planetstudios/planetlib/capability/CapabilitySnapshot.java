package com.planetstudios.planetlib.capability;

import com.planetstudios.planetlib.registry.ModRegistrySnapshot;

import java.util.List;\nimport java.util.Objects;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Immutable view of capabilities explicitly registered by loaded mods. */
public final class CapabilitySnapshot {
    private final List<CapabilityDescriptor> capabilities;
    private final Map<CapabilityId, CapabilityDescriptor> byId;

    CapabilitySnapshot(List<CapabilityDescriptor> capabilities) {
        this.capabilities = capabilities.stream()
                .sorted(java.util.Comparator.comparing(CapabilityDescriptor::id))
                .toList();
        this.byId = this.capabilities.stream()
                .collect(Collectors.toUnmodifiableMap(CapabilityDescriptor::id, item -> item));
    }

    public List<CapabilityDescriptor> getCapabilities() {
        return capabilities;
    }

    public Optional<CapabilityDescriptor> find(CapabilityId id) {
        return Optional.ofNullable(byId.get(Objects.requireNonNull(id, "id")));
    }

    /** True only when the capability was declared and its owning mod is loaded. */
    public boolean isAvailable(CapabilityId id, ModRegistrySnapshot mods) {
        Objects.requireNonNull(id, "id");\n        Objects.requireNonNull(mods, "mods");\n        return byId.containsKey(id) && mods.isLoaded(id.ownerModId());
    }

    public List<CapabilityDescriptor> findByOwner(String modId) {
        Objects.requireNonNull(modId, "modId");\n        return capabilities.stream().filter(item -> item.id().ownerModId().equals(modId)).toList();
    }

    public int size() {
        return capabilities.size();
    }
}
