package com.planetstudios.planetlib.essentials;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PlanetIntegrationTest {
    @Test
    void validatesAndCopiesIntegrationMetadata() {
        PlanetIntegration integration = new PlanetIntegration(
                "planet_projectile_optimizations",
                "Planet Projectile Optimizations",
                Set.of("planet:projectiles/vanilla", "planet:rendering/projectiles")
        );
        assertEquals("planet_projectile_optimizations", integration.modId());
        assertEquals(2, integration.capabilities().size());
        assertThrows(UnsupportedOperationException.class,
                () -> integration.capabilities().clear());
    }

    @Test
    void rejectsInvalidMetadata() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlanetIntegration("Planet Mod", "Name", Set.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new PlanetIntegration("planet_mod", " ", Set.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new PlanetIntegration("planet_mod", "Name", Set.of("bad-capability")));
    }
}
