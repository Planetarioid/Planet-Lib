package com.planetstudios.planetlib.compatibility;

import java.util.Objects;

/**
 * Immutable compatibility statement made by one integration mod about a
 * target mod. A declaration is descriptive metadata; it does not install
 * hooks, change settings, or perform compatibility tests.
 */
public record CompatibilityDeclaration(
        String integrationModId,
        String targetModId,
        CompatibilityLevel level,
        String summary,
        String conditions
) {
    public CompatibilityDeclaration {
        integrationModId = validateModId(integrationModId, "integrationModId");
        targetModId = validateModId(targetModId, "targetModId");
        level = Objects.requireNonNull(level, "level");
        summary = Objects.requireNonNull(summary, "summary").trim();
        conditions = Objects.requireNonNullElse(conditions, "").trim();

        if (summary.isBlank()) {
            throw new IllegalArgumentException("summary cannot be blank");
        }
        if (level == CompatibilityLevel.CONDITIONAL && conditions.isBlank()) {
            throw new IllegalArgumentException("Conditional compatibility requires conditions");
        }
        if (integrationModId.equals(targetModId)) {
            throw new IllegalArgumentException("A mod cannot declare compatibility with itself");
        }
    }

    private static String validateModId(String value, String field) {
        String id = Objects.requireNonNull(value, field).trim();
        if (!id.matches("[a-z0-9_.-]+") || id.equals(".") || id.equals("..")) {
            throw new IllegalArgumentException(field + " must be a valid lowercase mod ID");
        }
        return id;
    }
}
