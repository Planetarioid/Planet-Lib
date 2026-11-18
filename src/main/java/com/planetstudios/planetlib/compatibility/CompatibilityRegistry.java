package com.planetstudios.planetlib.compatibility;

import com.planetstudios.planetlib.registry.ModRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/** Registry for explicit compatibility declarations supplied by loaded mods. */
public final class CompatibilityRegistry {
    private final Predicate<String> isModLoaded;
    private final Map<Key, CompatibilityDeclaration> declarations = new HashMap<>();
    private final AtomicReference<CompatibilitySnapshot> snapshot =
            new AtomicReference<>(new CompatibilitySnapshot(List.of()));

    /** Creates a registry using the current Fabric-loaded mod snapshot. */
    public CompatibilityRegistry() {
        this(modId -> ModRegistry.snapshot().isLoaded(modId));
    }

    /** Creates an isolated registry with an explicit integration-mod presence check. */
    public CompatibilityRegistry(Predicate<String> isModLoaded) {
        this.isModLoaded = Objects.requireNonNull(isModLoaded, "isModLoaded");
    }

    /** Returns the shared process-wide compatibility registry. */
    public static CompatibilityRegistry getInstance() {
        return Holder.INSTANCE;
    }

    public synchronized CompatibilityDeclaration register(CompatibilityDeclaration declaration) {
        Objects.requireNonNull(declaration, "declaration");
        if (!isModLoaded.test(declaration.integrationModId())) {
            throw new IllegalArgumentException("Integration mod is not loaded: "
                    + declaration.integrationModId());
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

    private record Key(String integrationModId, String targetModId) { }

    private static final class Holder {
        private static final CompatibilityRegistry INSTANCE = new CompatibilityRegistry();
    }
}
