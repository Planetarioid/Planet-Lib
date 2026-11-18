package com.planetstudios.planetlib.registry;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An immutable point-in-time view of the mods known to Fabric Loader.
 */
public final class ModRegistrySnapshot {
    private final List<ModDescriptor> mods;
    private final List<ModDescriptor> planetMods;
    private final Map<String, ModDescriptor> modsById;

    ModRegistrySnapshot(List<ModDescriptor> mods) {
        this.mods = List.copyOf(mods);
        this.planetMods = this.mods.stream()
                .filter(ModDescriptor::planetMod)
                .toList();
        this.modsById = this.mods.stream()
                .collect(Collectors.toUnmodifiableMap(ModDescriptor::id, descriptor -> descriptor));
    }

    static ModRegistrySnapshot empty() {
        return new ModRegistrySnapshot(List.of());
    }

    public List<ModDescriptor> getMods() {
        return mods;
    }

    public List<ModDescriptor> getPlanetMods() {
        return planetMods;
    }

    public Optional<ModDescriptor> find(String modId) {
        return Optional.ofNullable(modsById.get(modId));
    }

    public boolean isLoaded(String modId) {
        return modsById.containsKey(modId);
    }

    public int getLoadedModCount() {
        return mods.size();
    }
}
