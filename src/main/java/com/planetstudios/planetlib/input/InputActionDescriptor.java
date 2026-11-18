package com.planetstudios.planetlib.input;
import java.util.Objects;
/**
 * Immutable metadata describing a user-triggered logical input action.
 * The default binding is opaque metadata. This descriptor does not read hardware,
 * register event listeners, inject input, or synthesize key presses.
 */
public record InputActionDescriptor(InputActionId id, String displayName, String description,
                                    String category, String defaultBinding) {
    public InputActionDescriptor {
        id = Objects.requireNonNull(id, "id");
        displayName = Objects.requireNonNull(displayName, "displayName").trim();
        if (displayName.isEmpty()) throw new IllegalArgumentException("displayName must not be blank");
        description = Objects.requireNonNullElse(description, "");
        category = Objects.requireNonNullElse(category, "general").trim();
        if (category.isEmpty() || !category.matches("[a-z0-9_.-]+")) {
            throw new IllegalArgumentException("category must be a non-empty lowercase identifier");
        }
        defaultBinding = Objects.requireNonNull(defaultBinding, "defaultBinding").trim();
        if (defaultBinding.isEmpty() || defaultBinding.length() > 128
                || !defaultBinding.matches("[a-zA-Z0-9_.:-]+")) {
            throw new IllegalArgumentException("defaultBinding must be a valid opaque binding identifier");
        }
    }
}
