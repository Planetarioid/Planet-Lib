package com.planetstudios.planetlib.compatibility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompatibilityDeclarationTest {
    @Test
    void acceptsValidDeclarationAndNormalizesText() {
        CompatibilityDeclaration declaration = new CompatibilityDeclaration(
                "planet_projectile_optimizations", "sodium",
                CompatibilityLevel.CONDITIONAL, " Coexists with supported renderer versions ",
                "Requires renderer compatibility mode."
        );
        assertEquals("Coexists with supported renderer versions", declaration.summary());
        assertEquals("Requires renderer compatibility mode.", declaration.conditions());
    }

    @Test
    void rejectsInvalidIdsAndMissingConditionalDetails() {
        assertThrows(IllegalArgumentException.class, () -> new CompatibilityDeclaration(
                "Planet Mod", "sodium", CompatibilityLevel.DETECT_ONLY, "Detected", ""));
        assertThrows(IllegalArgumentException.class, () -> new CompatibilityDeclaration(
                "planet_mod", "sodium", CompatibilityLevel.CONDITIONAL, "Conditional", ""));
        assertThrows(IllegalArgumentException.class, () -> new CompatibilityDeclaration(
                "planet_mod", "planet_mod", CompatibilityLevel.TESTED_COEXISTENCE, "Tested", ""));
        assertThrows(IllegalArgumentException.class, () -> new CompatibilityDeclaration(
                "planet_mod", "sodium", CompatibilityLevel.NATIVE_INTEGRATION, " ", ""));
    }
}
