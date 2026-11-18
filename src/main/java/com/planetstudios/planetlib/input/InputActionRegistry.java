package com.planetstudios.planetlib.input;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
/**
 * Explicit registry for logical input-action metadata.
 * Creating or using this registry does not install input hooks or activate an input engine.
 */
public final class InputActionRegistry {
    private final Map<InputActionId, InputActionDescriptor> registered = new HashMap<>();
    private final AtomicReference<InputActionSnapshot> snapshot =
            new AtomicReference<>(new InputActionSnapshot(java.util.List.of()));
    /** Registers an action during mod initialization. */
    public synchronized InputActionDescriptor register(InputActionDescriptor descriptor) {
        Objects.requireNonNull(descriptor, "descriptor");
        InputActionId id = descriptor.id();
        if (registered.containsKey(id)) throw new IllegalArgumentException("Input action already registered: " + id);
        registered.put(id, descriptor);
        snapshot.set(new InputActionSnapshot(registered.values()));
        return descriptor;
    }
    /** Returns the latest immutable snapshot. */
    public InputActionSnapshot snapshot() { return snapshot.get(); }
}
