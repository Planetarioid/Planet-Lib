package com.planetstudios.planetlib.resource;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ResourceRegistryTest {
    private ResourceDescriptor texture(String path) {
        return new ResourceDescriptor(new ResourceId("planet_essentials", path),
                "planet_essentials", ResourceKind.TEXTURE, "UI texture", Set.of("ui"));
    }

    @Test
    void parsesAndOrdersNamespacedIds() {
        assertEquals(new ResourceId("planet_essentials", "textures/ui/icon.png"),
                ResourceId.parse("planet_essentials:textures/ui/icon.png"));
        assertTrue(new ResourceId("a", "z").compareTo(new ResourceId("b", "a")) < 0);
    }

    @Test
    void rejectsMalformedAndTraversingIds() {
        assertThrows(IllegalArgumentException.class, () -> ResourceId.parse("missing-separator"));
        assertThrows(IllegalArgumentException.class, () -> new ResourceId("Bad Namespace", "icon.png"));
        assertThrows(IllegalArgumentException.class, () -> new ResourceId("example", "../secret"));
        assertThrows(IllegalArgumentException.class, () -> new ResourceId("example", "textures//icon.png"));
        assertThrows(IllegalArgumentException.class, () -> new ResourceId("example", "/icon.png"));
    }

    @Test
    void registersAndQueriesImmutableSnapshots() {
        ResourceRegistry registry = new ResourceRegistry(id -> id.equals("planet_essentials"));
        registry.register(texture("textures/ui/b.png"));
        registry.register(texture("textures/ui/a.png"));

        ResourceRegistrySnapshot snapshot = registry.snapshot();
        assertEquals(2, snapshot.size());
        assertEquals("planet_essentials:textures/ui/a.png",
                snapshot.resources().get(0).id().toString());
        assertTrue(snapshot.contains(new ResourceId("planet_essentials", "textures/ui/a.png")));
        assertEquals(2, snapshot.byOwner("planet_essentials").size());
        assertEquals(2, snapshot.byKind(ResourceKind.TEXTURE).size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.resources().clear());
    }

    @Test
    void rejectsDuplicateIdsAndOwnerNamespaceMismatch() {
        ResourceRegistry registry = new ResourceRegistry();
        registry.register(texture("textures/ui/icon.png"));
        assertThrows(IllegalArgumentException.class,
                () -> registry.register(texture("textures/ui/icon.png")));
        assertThrows(IllegalArgumentException.class, () -> new ResourceDescriptor(
                new ResourceId("another_mod", "icon"), "planet_essentials",
                ResourceKind.OTHER, "", null));
    }

    @Test
    void snapshotsRemainStableAfterLaterRegistration() {
        ResourceRegistry registry = new ResourceRegistry();
        registry.register(texture("textures/ui/first.png"));
        ResourceRegistrySnapshot earlier = registry.snapshot();
        registry.register(texture("textures/ui/second.png"));

        assertEquals(1, earlier.size());
        assertEquals(2, registry.snapshot().size());
    }
}
