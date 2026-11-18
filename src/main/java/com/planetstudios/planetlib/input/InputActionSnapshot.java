package com.planetstudios.planetlib.input;
import java.util.List;
import java.util.Map;
import java.util.Optional;
/** Immutable, deterministically ordered view of registered logical input actions. */
public final class InputActionSnapshot {
    private final List<InputActionDescriptor> actions;
    private final Map<InputActionId, InputActionDescriptor> byId;
    public InputActionSnapshot(Iterable<InputActionDescriptor> descriptors) {
        java.util.ArrayList<InputActionDescriptor> sorted = new java.util.ArrayList<>();
        for (InputActionDescriptor descriptor : descriptors) sorted.add(java.util.Objects.requireNonNull(descriptor, "descriptor"));
        sorted.sort(java.util.Comparator.comparing(InputActionDescriptor::id));
        this.actions = List.copyOf(sorted);
        java.util.LinkedHashMap<InputActionId, InputActionDescriptor> index = new java.util.LinkedHashMap<>();
        for (InputActionDescriptor descriptor : actions) {
            if (index.putIfAbsent(descriptor.id(), descriptor) != null) {
                throw new IllegalArgumentException("Duplicate input action: " + descriptor.id());
            }
        }
        this.byId = Map.copyOf(index);
    }
    public List<InputActionDescriptor> getActions() { return actions; }
    public Optional<InputActionDescriptor> find(InputActionId id) {
        return Optional.ofNullable(byId.get(java.util.Objects.requireNonNull(id, "id")));
    }
    public boolean contains(InputActionId id) {
        return byId.containsKey(java.util.Objects.requireNonNull(id, "id"));
    }
    public List<InputActionDescriptor> getActionsForMod(String modId) {
        java.util.Objects.requireNonNull(modId, "modId");
        return actions.stream().filter(action -> action.id().modId().equals(modId)).toList();
    }
    public int size() { return actions.size(); }
}
