package com.planetstudios.planetlib.compatibility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Immutable, deterministically ordered compatibility declaration snapshot. */
public final class CompatibilitySnapshot {
    private final List<CompatibilityDeclaration> declarations;

    CompatibilitySnapshot(Collection<CompatibilityDeclaration> declarations) {
        Objects.requireNonNull(declarations, "declarations");
        List<CompatibilityDeclaration> sorted = new ArrayList<>(declarations);
        sorted.sort(Comparator.comparing(CompatibilityDeclaration::integrationModId)
                .thenComparing(CompatibilityDeclaration::targetModId));
        this.declarations = List.copyOf(sorted);
    }

    public List<CompatibilityDeclaration> getDeclarations() {
        return declarations;
    }

    public Optional<CompatibilityDeclaration> find(String integrationModId, String targetModId) {
        Objects.requireNonNull(integrationModId, "integrationModId");
        Objects.requireNonNull(targetModId, "targetModId");
        return declarations.stream()
                .filter(item -> item.integrationModId().equals(integrationModId)
                        && item.targetModId().equals(targetModId))
                .findFirst();
    }

    public List<CompatibilityDeclaration> findForTarget(String targetModId) {
        Objects.requireNonNull(targetModId, "targetModId");
        return declarations.stream()
                .filter(item -> item.targetModId().equals(targetModId))
                .toList();
    }

    public int size() {
        return declarations.size();
    }
}
