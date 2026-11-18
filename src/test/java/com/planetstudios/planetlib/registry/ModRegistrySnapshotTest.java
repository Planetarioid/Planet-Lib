package com.planetstudios.planetlib.registry;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ModRegistrySnapshotTest {
    private ModDescriptor descriptor(String id, boolean planet) {
        return new ModDescriptor(id, id, "1.0.0", "", "fabric", Set.of(), null, planet);
    }

    @Test
    void exposesStableLookupAndPlanetSubset() {
        ModDescriptor sodium = descriptor("sodium", false);
        ModDescriptor essentials = descriptor("planet_essentials", true);
        ModRegistrySnapshot snapshot = new ModRegistrySnapshot(List.of(sodium, essentials));

        assertEquals(2, snapshot.getLoadedModCount());
        assertTrue(snapshot.isLoaded("sodium"));
        assertEquals(sodium, snapshot.find("sodium").orElseThrow());
        assertEquals(List.of(essentials), snapshot.getPlanetMods());
        assertFalse(snapshot.isLoaded("missing_mod"));
    }

    @Test
    void exposesImmutableCollections() {
        ModRegistrySnapshot snapshot = new ModRegistrySnapshot(
                List.of(descriptor("planet_lib", true)));

        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.getMods().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.getPlanetMods().clear());
    }
}
