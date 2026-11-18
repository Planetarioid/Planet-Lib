package com.planetstudios.planetlib.registry;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ModIdentityTest {
    @Test
    void recognizesCanonicalPlanetModIds() {
        assertTrue(ModIdentity.isPlanetMod("planet_essentials", Set.of()));
        assertTrue(ModIdentity.isPlanetMod("planet_lib", Set.of()));
    }

    @Test
    void recognizesPlanetIdsProvidedAsAliases() {
        assertTrue(ModIdentity.isPlanetMod("third_party_wrapper", Set.of("planet_essentials")));
    }

    @Test
    void doesNotUseDisplayNamesOrUnrelatedIds() {
        assertFalse(ModIdentity.isPlanetMod("essentials", Set.of()));
        assertFalse(ModIdentity.isPlanetMod("planetarium", Set.of()));
        assertFalse(ModIdentity.isPlanetMod("sodium", Set.of("sodium_extra")));
    }

    @Test
    void safelyHandlesNullAliasCollection() {
        assertFalse(ModIdentity.isPlanetMod("sodium", null));
    }
}
