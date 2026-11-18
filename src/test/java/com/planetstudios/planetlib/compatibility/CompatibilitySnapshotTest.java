package com.planetstudios.planetlib.compatibility;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompatibilitySnapshotTest {
    @Test
    void sortsAndFindsDeclarationsWithoutExposingMutableState() {
        CompatibilityDeclaration second = declaration("planet_beta", "sodium");
        CompatibilityDeclaration first = declaration("planet_alpha", "fabric_api");
        CompatibilitySnapshot snapshot = new CompatibilitySnapshot(List.of(second, first));

        assertEquals(first, snapshot.find("planet_alpha", "fabric_api").orElseThrow());
        assertEquals(List.of(first, second), snapshot.getDeclarations());
        assertEquals(List.of(second), snapshot.findForTarget("sodium"));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.getDeclarations().clear());
    }

    @Test
    void missingDeclarationsRemainUnknownRatherThanAssumedCompatible() {
        CompatibilitySnapshot snapshot = new CompatibilitySnapshot(List.of());
        assertTrue(snapshot.find("planet_alpha", "unknown_mod").isEmpty());
        assertEquals(0, snapshot.size());
    }

    private static CompatibilityDeclaration declaration(String owner, String target) {
        return new CompatibilityDeclaration(owner, target, CompatibilityLevel.DETECT_ONLY,
                "Mod presence can be identified.", "");
    }
}
