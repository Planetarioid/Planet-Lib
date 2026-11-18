package com.planetstudios.planetlib.essentials;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Immutable metadata describing a Planet Studios mod that can integrate
 * with Planet Essentials.
 */
public record PlanetIntegration(String modId, String displayName, Set<String> capabilities) {
    public PlanetIntegration {
        modId = Objects.requireNonNull(modId, "modId").trim();
        displayName = Objects.requireNonNull(displayName, "displayName").trim();
        Objects.requireNonNull(capabilities, "capabilities");

        if (!modId.matches("[a-z0-9_.-]+") || modId.equals(".") || modId.equals("..")) {
            throw new IllegalArgumentException("modId must be a valid lowercase mod identifier");
        }
        if (displayName.isBlank()) {
            throw new IllegalArgumentException("displayName cannot be blank");
        }

        TreeSet<String> normalized = new TreeSet<>();
        for (String capability : capabilities) {
            String value = Objects.requireNonNull(capability, "capability").trim();
            if (!value.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")) {
                throw new IllegalArgumentException("Invalid capability identifier: " + value);
            }
            normalized.add(value);
        }
        capabilities = Set.copyOf(normalized);
    }
}
