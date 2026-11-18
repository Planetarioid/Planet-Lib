package com.planetstudios.planetlib.feature;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class FeaturePriorityManagerTest {
    @Test
    void selectsHighestPriorityImplementationDeterministically() {
        FeatureRegistry registry = registryWithFeatures();
        FeaturePriorityManager manager = new FeaturePriorityManager(registry);
        FeatureId alpha = new FeatureId("planet_alpha", "renderer");
        FeatureId beta = new FeatureId("planet_beta", "renderer");
        manager.register(new FeatureImplementation("planet:rendering/entity", alpha, 10));
        manager.register(new FeatureImplementation("planet:rendering/entity", beta, 20));
        FeatureResolution resolution = manager.findResolution("planet:rendering/entity").orElseThrow();
        assertEquals(beta, resolution.selected().featureId());
        assertFalse(resolution.isUserPreferred());
        assertEquals(beta, resolution.implementations().get(0).featureId());
    }

    @Test
    void equalPrioritiesUseFeatureIdAsStableTieBreaker() {
        FeatureRegistry registry = registryWithFeatures();
        FeaturePriorityManager manager = new FeaturePriorityManager(registry);
        FeatureId alpha = new FeatureId("planet_alpha", "renderer");
        FeatureId beta = new FeatureId("planet_beta", "renderer");
        manager.register(new FeatureImplementation("planet:rendering/entity", beta, 5));
        manager.register(new FeatureImplementation("planet:rendering/entity", alpha, 5));
        assertEquals(alpha, manager.findResolution("planet:rendering/entity").orElseThrow().selected().featureId());
    }

    @Test
    void userPreferenceOverridesPriorityAndCanBeReversed() {
        FeatureRegistry registry = registryWithFeatures();
        FeaturePriorityManager manager = new FeaturePriorityManager(registry);
        FeatureId alpha = new FeatureId("planet_alpha", "renderer");
        FeatureId beta = new FeatureId("planet_beta", "renderer");
        manager.register(new FeatureImplementation("planet:rendering/entity", alpha, 1));
        manager.register(new FeatureImplementation("planet:rendering/entity", beta, 99));
        FeaturePrioritySnapshot preferred = manager.preferImplementation("planet:rendering/entity", alpha);
        assertEquals(alpha, preferred.find("planet:rendering/entity").orElseThrow().selected().featureId());
        assertTrue(preferred.find("planet:rendering/entity").orElseThrow().isUserPreferred());
        FeaturePrioritySnapshot restored = manager.clearPreference("planet:rendering/entity");
        assertEquals(beta, restored.find("planet:rendering/entity").orElseThrow().selected().featureId());
        assertFalse(restored.find("planet:rendering/entity").orElseThrow().isUserPreferred());
    }

    @Test
    void rejectsUnknownFeaturesAndInvalidPreferences() {
        FeatureRegistry registry = registryWithFeatures();
        FeaturePriorityManager manager = new FeaturePriorityManager(registry);
        FeatureId unknown = new FeatureId("planet_missing", "renderer");
        assertThrows(IllegalArgumentException.class,
                () -> manager.register(new FeatureImplementation("planet:rendering/entity", unknown, 1)));
        assertThrows(IllegalArgumentException.class,
                () -> manager.preferImplementation("planet:rendering/entity", unknown));
    }

    @Test
    void rejectsDuplicateProvidersAndInvalidCapabilityIds() {
        FeatureRegistry registry = registryWithFeatures();
        FeaturePriorityManager manager = new FeaturePriorityManager(registry);
        FeatureId alpha = new FeatureId("planet_alpha", "renderer");
        manager.register(new FeatureImplementation("planet:rendering/entity", alpha, 1));
        assertThrows(IllegalArgumentException.class,
                () -> manager.register(new FeatureImplementation("planet:rendering/entity", alpha, 2)));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureImplementation("Planet:Rendering", alpha, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new FeatureImplementation("planet:rendering/../entity", alpha, 1));
    }

    @Test
    void resolvesCapabilitiesIndependentlyAndPublishesImmutableSnapshots() {
        FeatureRegistry registry = registryWithFeatures();
        FeaturePriorityManager manager = new FeaturePriorityManager(registry);
        FeatureId alpha = new FeatureId("planet_alpha", "renderer");
        FeatureId beta = new FeatureId("planet_beta", "renderer");
        manager.register(new FeatureImplementation("planet:rendering/entity", alpha, 1));
        manager.register(new FeatureImplementation("planet:audio/mixing", beta, 1));
        FeaturePrioritySnapshot snapshot = manager.snapshot();
        assertEquals(2, snapshot.size());
        assertTrue(snapshot.find("planet:audio/mixing").isPresent());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.getResolutions().clear());
    }

    private static FeatureRegistry registryWithFeatures() {
        FeatureRegistry registry = new FeatureRegistry();
        registry.register(feature("planet_alpha"));
        registry.register(feature("planet_beta"));
        return registry;
    }

    private static FeatureDescriptor feature(String modId) {
        return new FeatureDescriptor(new FeatureId(modId, "renderer"), "Renderer",
                "Test renderer", "rendering", Set.of("client"));
    }
}
