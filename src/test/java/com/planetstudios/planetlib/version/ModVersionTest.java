package com.planetstudios.planetlib.version;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModVersionTest {
    @Test
    void parsesCommonVersionForms() {
        assertEquals(0, ModVersion.parse("1.2.0").compareTo(ModVersion.parse("v1.2")));
        assertEquals("beta.2", ModVersion.parse("2.0.0-beta.2+build.4").prerelease());
    }

    @Test
    void comparesNumericComponentsAndPrereleases() {
        assertTrue(ModVersion.parse("1.10").compareTo(ModVersion.parse("1.9.9")) > 0);
        assertTrue(ModVersion.parse("2.0.0").compareTo(ModVersion.parse("2.0.0-rc.1")) > 0);
        assertTrue(ModVersion.parse("2.0.0-beta.2").compareTo(ModVersion.parse("2.0.0-beta.10")) < 0);
        assertEquals(0, ModVersion.parse("1.2").compareTo(ModVersion.parse("1.2.0")));
    }

    @Test
    void rejectsUnrecognizedAndOverflowingVersions() {
        assertThrows(IllegalArgumentException.class, () -> ModVersion.parse("snapshot-build"));
        assertThrows(IllegalArgumentException.class, () -> ModVersion.parse("999999999999999999999"));
    }
}
