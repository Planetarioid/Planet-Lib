package com.planetstudios.planetlib.compatibility;

import com.planetstudios.planetlib.registry.ModRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Registry for explicit compatibility declarations supplied by loaded mods.
 *
 * <p>The declaring integration mod must be loaded according to Fabric's
 * ModRegistry. The target may be absent, allowing declarations to describe
 * supported combinations before the target is installed. This registry
 * reports declarations only; it does not infer or verify compatibility.</p>
 */
public final class CompatibilityRegistry {
    private final Map<Key, CompatibilityDeclaration> declarations = new HashMap<>();
    private final AtomicReference<CompatibilitySnapshot> snapshot =
            new AtomicReference<>(new CompatibilitySnapshot(List.of()));

    /**
     * Adds a declaration from a currently loaded integration mod.
     *
     * @throws IllegalArgumentException if the declaring mod is not loaded or
     *                                  the same pair has already been declared
     */
    public synchronized CompatibilityDeclaration register(CompatibilityDeclaration declaration) {
        Objects.requireNonNull(declaration, "declaration");
        if (!ModRegistry.snapshot().isLoaded(declaration.integrationModId())) {
            throw new IllegalArgumentException(
                    "Integration mod is not loaded: " + declaration.integrationModId());
        }

        Key key = new Key(declaration.integrationModId(), declaration.targetModId());
        if (declarations.putIfAbsent(key, declaration) != null) {
            throw new IllegalArgumentException("Compatibility already declared for "
                    + declaration.integrationModId() + " -> " + declaration.targetModId());
        }
        rebuildSnapshot();
        return declaration;
    }

    public CompatibilitySnapshot snapshot() {
        return snapshot.get();
    }

    private void rebuildSnapshot() {
        snapshot.set(new CompatibilitySnapshot(new ArrayList<>(declarations.values())));
    }

    private record Key(String integrationModId, String targetModId) {
    }
}
