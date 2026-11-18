package com.planetstudios.planetlib.capability;

import java.util.Objects;

/** Stable owner-namespaced identifier for a capability provided by a mod. */
public record CapabilityId(String ownerModId, String name) implements Comparable<CapabilityId> {
    public CapabilityId {
        ownerModId = validate(ownerModId, "ownerModId");
        name = validate(name, "name");
    }

    private static String validate(String value, String field) {
        String checked = Objects.requireNonNull(value, field).trim();
        if (!checked.matches("[a-z0-9_.-]+") || checked.equals(".") || checked.equals("..")) {
            throw new IllegalArgumentException(field + " must use lowercase letters, digits, _, . or -");
        }
        return checked;
    }

    @Override
    public int compareTo(CapabilityId other) {
        int owner = ownerModId.compareTo(other.ownerModId);
        return owner != 0 ? owner : name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return ownerModId + ":" + name;
    }
}
