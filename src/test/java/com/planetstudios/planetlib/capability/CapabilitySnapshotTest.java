package com.planetstudios.planetlib.capability;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CapabilitySnapshotTest {
    @Test
    void sortsFindsAndGroupsCapabilities() {
        CapabilityDescriptor second = descriptor("planet_beta", "render_hook");
        CapabilityDescriptor first = descriptor("planet_alpha", "config_api");
        CapabilitySnapshot snapshot = new CapabilitySnapshot(List.of(second, first));

        assertEquals(first, snapshot.find(first.id()).orElseThrow());
        assertEquals(List.of(first), snapshot.findByOwner("planet_alpha"));
        assertEquals(List.of(first, second), snapshot.getCapabilities());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.getCapabilities().clear());
    }

    @Test
    void capabilityIdsAreNamespacedAndValidated() {
        assertEquals("planet_alpha:config_api", new CapabilityId("planet_alpha", "config_api").toString());
        assertThrows(IllegalArgumentException.class, () -> new CapabilityId("Planet", "config_api"));
        assertThrows(IllegalArgumentException.class, () -> new CapabilityId("..", "config_api"));
        assertThrows(IllegalArgumentException.class, () -> new CapabilityId("planet_alpha", ".."));
    }

    private static CapabilityDescriptor descriptor(String owner, String name) {
        return new CapabilityDescriptor(new CapabilityId(owner, name), "Test capability.");
    }
}
