package com.planetstudios.planetlib.input;
import java.util.Objects;
/** Stable identifier for a logical input action owned by a mod. */
public record InputActionId(String modId, String actionId) implements Comparable<InputActionId> {
    public InputActionId {
        validate(modId, "modId");
        validate(actionId, "actionId");
    }
    private static void validate(String value, String name) {
        Objects.requireNonNull(value, name);
        if (!value.matches("[a-z0-9_.-]+") || value.equals(".") || value.equals("..")) {
            throw new IllegalArgumentException(name + " must contain only lowercase letters, digits, '.', '_' or '-'");
        }
    }
    @Override public int compareTo(InputActionId other) {
        int ownerComparison = modId.compareTo(other.modId);
        return ownerComparison != 0 ? ownerComparison : actionId.compareTo(other.actionId);
    }
    @Override public String toString() { return modId + ":" + actionId; }
}
