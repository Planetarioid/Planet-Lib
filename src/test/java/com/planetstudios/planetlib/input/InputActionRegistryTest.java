package com.planetstudios.planetlib.input;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class InputActionRegistryTest {
    @Test void registersAndFindsActions() {
        InputActionRegistry registry = new InputActionRegistry();
        InputActionDescriptor descriptor = action("planet_projectile_optimizations", "toggle-overlay");
        registry.register(descriptor);
        InputActionSnapshot snapshot = registry.snapshot();
        assertEquals(1, snapshot.size());
        assertTrue(snapshot.contains(descriptor.id()));
        assertEquals(descriptor, snapshot.find(descriptor.id()).orElseThrow());
        assertEquals(1, snapshot.getActionsForMod("planet_projectile_optimizations").size());
    }
    @Test void allowsSameActionNameForDifferentMods() {
        InputActionRegistry registry = new InputActionRegistry();
        InputActionDescriptor first = action("planet_projectile_optimizations", "toggle");
        InputActionDescriptor second = action("planet_totem_optimizer", "toggle");
        registry.register(first); registry.register(second);
        assertEquals(2, registry.snapshot().size());
        assertNotEquals(first.id(), second.id());
    }
    @Test void rejectsDuplicateIds() {
        InputActionRegistry registry = new InputActionRegistry();
        InputActionDescriptor descriptor = action("planet_test", "toggle");
        registry.register(descriptor);
        assertThrows(IllegalArgumentException.class, () -> registry.register(descriptor));
    }
    @Test void snapshotsAreImmutableAndSorted() {
        InputActionRegistry registry = new InputActionRegistry();
        InputActionDescriptor later = action("planet_test", "z-action");
        InputActionDescriptor earlier = action("planet_test", "a-action");
        registry.register(later); registry.register(earlier);
        InputActionSnapshot snapshot = registry.snapshot();
        assertEquals(earlier.id(), snapshot.getActions().get(0).id());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.getActions().add(later));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.getActionsForMod("planet_test").clear());
    }
    @Test void validatesIdentifiersAndBindingMetadata() {
        assertThrows(IllegalArgumentException.class, () -> new InputActionId("../planet_test", "toggle"));
        assertThrows(IllegalArgumentException.class, () -> new InputActionId("planet_test", "../toggle"));
        assertThrows(IllegalArgumentException.class, () -> new InputActionDescriptor(
                new InputActionId("planet_test", "toggle"), " ", "", "general", "key.keyboard.g"));
        assertThrows(IllegalArgumentException.class, () -> new InputActionDescriptor(
                new InputActionId("planet_test", "toggle"), "Toggle", "", "Bad Category", "key.keyboard.g"));
        assertThrows(IllegalArgumentException.class, () -> new InputActionDescriptor(
                new InputActionId("planet_test", "toggle"), "Toggle", "", "general", "../key"));
    }
    private static InputActionDescriptor action(String modId, String actionId) {
        return new InputActionDescriptor(new InputActionId(modId, actionId), "Test " + actionId,
                "Test input action", "general", "key.keyboard.g");
    }
}
