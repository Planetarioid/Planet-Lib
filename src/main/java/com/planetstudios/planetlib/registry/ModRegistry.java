package com.planetstudios.planetlib.registry;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Read-only registry of mods successfully loaded by Fabric Loader.
 *
 * <p>Fabric Loader owns mod discovery and loading. This class deliberately
 * does not scan the mods directory, inspect arbitrary archives, or load mods.
 * Refreshing creates a new immutable snapshot and atomically publishes it.</p>
 */
public final class ModRegistry {
    private static final AtomicReference<ModRegistrySnapshot> CURRENT =
            new AtomicReference<>(ModRegistrySnapshot.empty());

    private ModRegistry() {
    }

    public static ModRegistrySnapshot snapshot() {
        return CURRENT.get();
    }

    /**
     * Rebuilds the snapshot from Fabric Loader's current loaded-mod list.
     * Intended for initialization and explicit diagnostics, not per-tick polling.
     */
    public static ModRegistrySnapshot refresh() {
        var descriptors = FabricLoader.getInstance().getAllMods().stream()
                .map(ModRegistry::describe)
                .sorted(Comparator.comparing(ModDescriptor::id))
                .toList();

        ModRegistrySnapshot updated = new ModRegistrySnapshot(descriptors);
        CURRENT.set(updated);
        return updated;
    }

    private static ModDescriptor describe(ModContainer container) {
        ModMetadata metadata = container.getMetadata();
        Set<String> providedIds = Set.copyOf(metadata.getProvides());

        String parentId = container.getContainingMod()
                .map(parent -> parent.getMetadata().getId())
                .orElse(null);

        return new ModDescriptor(
                metadata.getId(),
                metadata.getName(),
                metadata.getVersion().getFriendlyString(),
                metadata.getDescription(),
                metadata.getType(),
                providedIds,
                parentId,
                ModIdentity.isPlanetMod(metadata.getId(), providedIds)
        );
    }
}
