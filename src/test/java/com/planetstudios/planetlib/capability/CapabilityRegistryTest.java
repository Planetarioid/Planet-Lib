package com.planetstudios.planetlib.capability;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CapabilityRegistryTest {
    @Test void requiresLoadedOwnerAndRejectsDuplicates() {
        CapabilityRegistry registry = new CapabilityRegistry("planet_provider"::equals);
        CapabilityDescriptor descriptor = new CapabilityDescriptor(
                new CapabilityId("planet_provider", "render_api"), "Rendering API");
        assertThrows(IllegalArgumentException.class, () -> registry.register(
                new CapabilityDescriptor(new CapabilityId("planet_missing", "render_api"), "Rendering API")));
        registry.register(descriptor);
        assertEquals(descriptor, registry.snapshot().find(descriptor.id()).orElseThrow());
        assertThrows(IllegalArgumentException.class, () -> registry.register(descriptor));
    }

    @Test void snapshotsAreImmutableAndStable() {
        CapabilityRegistry registry = new CapabilityRegistry(id -> true);
        CapabilityDescriptor first = new CapabilityDescriptor(
                new CapabilityId("planet_a", "first"), "First");
        CapabilityDescriptor second = new CapabilityDescriptor(
                new CapabilityId("planet_b", "second"), "Second");
        registry.register(first);
        CapabilitySnapshot before = registry.snapshot();
        registry.register(second);
        assertEquals(1, before.size());
        assertEquals(2, registry.snapshot().size());
        assertThrows(UnsupportedOperationException.class, () -> registry.snapshot().getCapabilities().clear());
    }
}
