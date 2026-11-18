package com.planetstudios.planetlib.version;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionRequirementTest {
    @Test
    void appliesInclusiveMinimumAndExclusiveMaximum() {
        VersionRequirement requirement = VersionRequirement.between("1.4.0", "2.0.0");
        assertTrue(requirement.matches(ModVersion.parse("1.4.0")));
        assertTrue(requirement.matches(ModVersion.parse("1.9.9")));
        assertFalse(requirement.matches(ModVersion.parse("2.0.0")));
        assertFalse(requirement.matches(ModVersion.parse("1.3.9")));
    }

    @Test
    void rejectsInvertedRanges() {
        assertThrows(IllegalArgumentException.class,
                () -> VersionRequirement.between("2.0", "1.0"));
    }
}
