package com.planetstudios.planetlib.feature;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FeatureRegistryTest {
    @Test
    void registersAndFindsFeaturesByNamespacedId() {
        FeatureRegistry registry = new FeatureRegistry();
        FeatureDescriptor descriptor = feature("planet_projectile_optimizations", "arrow-rendering");

        registry.register(descriptor);

        FeatureRegistrySnapshot snapshot = registry.snapshot();
        assertEquals(1, snapshot.size());
        assertTrue(snapshot.isRegistered(descriptor.id()));
        assertEquals(descriptor, snapshot.find(descriptor.id()).orElseThrow());
        assertEquals(1, snapshot.getFeaturesForMod("planet_projectile_optimizations").size());
    }

    @Test
    void allowsSameFeatureNameForDifferentOwningMods() {
        FeatureRegistry registry = new FeatureRegistry();
        FeatureDescriptor projectile = feature("planet_projectile_optimizations", "rendering");
        FeatureDescriptor totem = feature("planet_totem_optimizer", "rendering");

        registry.register(projectile);
        registry.register(totem);

        assertEquals(2, registry.snapshot().size());
        assertNotEquals(projectile.id(), totem.id());
    }

    @Test
    void rejectsDuplicateFeatureIdentifiers() {
        FeatureRegistry registry = new FeatureRegistry();
        FeatureDescriptor descriptor = feature("planet_test", "rendering");

        registry.register(descriptor);

        assertThrows(IllegalArgumentException.class, () -> registry.register(descriptor));
    }

    @Test
    void snapshotsAreImmutableAndSorted() {
        FeatureRegistry registry = new FeatureRegistry();
        FeatureDescriptor second = feature("planet_test", "z-feature");
        FeatureDescriptor first = feature("planet_test", "a-feature");

        registry.register(second);
        registry.register(first);

        FeatureRegistrySnapshot snapshot = registry.snapshot();
        assertEquals(first.id(), snapshot.getFeatures().get(0).id());
        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.getFeatures().add(second));
        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.getFeaturesForMod("planet_test").clear());
    }

    @Test
    void validatesIdentifiersAndMetadata() {
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureId("../planet_test", "rendering"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureId("planet_test", "../rendering"));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureDescriptor(new FeatureId("planet_test", "rendering"),
                        " ", "", "general", Set.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureDescriptor(new FeatureId("planet_test", "rendering"),
                        "Rendering", "", "Bad Category", Set.of()));
    }

    private static FeatureDescriptor feature(String modId, String featureId) {
        return new FeatureDescriptor(
                new FeatureId(modId, featureId),
                "Test " + featureId,
                "Test feature description",
                "rendering",
                Set.of("client", "optimization")
        );
    }
}
