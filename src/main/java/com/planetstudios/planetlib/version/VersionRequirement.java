package com.planetstudios.planetlib.version;

import java.util.Objects;

/** Inclusive minimum and exclusive maximum version bounds. Null means unbounded. */
public record VersionRequirement(ModVersion minimumInclusive, ModVersion maximumExclusive) {
    public VersionRequirement {
        if (minimumInclusive != null && maximumExclusive != null
                && minimumInclusive.compareTo(maximumExclusive) >= 0) {
            throw new IllegalArgumentException("Minimum version must be below maximum version");
        }
    }

    public static VersionRequirement any() {
        return new VersionRequirement(null, null);
    }

    public static VersionRequirement atLeast(String minimum) {
        return new VersionRequirement(ModVersion.parse(minimum), null);
    }

    public static VersionRequirement between(String minimumInclusive, String maximumExclusive) {
        return new VersionRequirement(ModVersion.parse(minimumInclusive), ModVersion.parse(maximumExclusive));
    }

    public boolean matches(ModVersion version) {
        Objects.requireNonNull(version, "version");
        return (minimumInclusive == null || version.compareTo(minimumInclusive) >= 0)
                && (maximumExclusive == null || version.compareTo(maximumExclusive) < 0);
    }
}
