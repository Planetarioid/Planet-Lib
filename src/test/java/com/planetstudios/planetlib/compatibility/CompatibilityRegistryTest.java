package com.planetstudios.planetlib.compatibility;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompatibilityRegistryTest {
    @Test void requiresLoadedDeclaringModAndRejectsDuplicatePair() {
        CompatibilityRegistry registry = new CompatibilityRegistry("planet_integration"::equals);
        CompatibilityDeclaration declaration = new CompatibilityDeclaration(
                "planet_integration", "sodium", CompatibilityLevel.DETECT_ONLY, "Presence detection only.", "");
        assertThrows(IllegalArgumentException.class, () -> registry.register(
                new CompatibilityDeclaration("planet_absent", "sodium",
                        CompatibilityLevel.DETECT_ONLY, "Presence detection only.", "")));
        registry.register(declaration);
        assertEquals(declaration, registry.snapshot().find("planet_integration", "sodium").orElseThrow());
        assertThrows(IllegalArgumentException.class, () -> registry.register(declaration));
    }

    @Test void publishesImmutableSnapshots() {
        CompatibilityRegistry registry = new CompatibilityRegistry(id -> true);
        registry.register(new CompatibilityDeclaration("planet_a", "target_a",
                CompatibilityLevel.DETECT_ONLY, "Detected.", ""));
        CompatibilitySnapshot snapshot = registry.snapshot();
        registry.register(new CompatibilityDeclaration("planet_b", "target_b",
                CompatibilityLevel.TESTED_COEXISTENCE, "Tested.", ""));
        assertEquals(1, snapshot.size());
        assertEquals(2, registry.snapshot().size());
        assertThrows(UnsupportedOperationException.class, () -> registry.snapshot().getDeclarations().clear());
    }
}
