package com.planetstudios.planetlib.version;

import com.planetstudios.planetlib.registry.ModDescriptor;
import com.planetstudios.planetlib.registry.ModRegistrySnapshot;

import java.util.Objects;

/** Performs read-only version checks against an existing loaded-mod snapshot. */
public final class VersionDetector {
    private VersionDetector() {
    }

    public static VersionCheckResult check(
            ModRegistrySnapshot snapshot, String modId, VersionRequirement requirement) {
        Objects.requireNonNull(snapshot, "snapshot");
        Objects.requireNonNull(modId, "modId");
        Objects.requireNonNull(requirement, "requirement");

        ModDescriptor mod = snapshot.find(modId).orElse(null);
        if (mod == null) return VersionCheckResult.MOD_NOT_LOADED;

        final ModVersion version;
        try {
            version = ModVersion.parse(mod.version());
        } catch (IllegalArgumentException exception) {
            return VersionCheckResult.VERSION_UNPARSEABLE;
        }

        if (requirement.minimumInclusive() != null
                && version.compareTo(requirement.minimumInclusive()) < 0) {
            return VersionCheckResult.BELOW_MINIMUM;
        }
        if (requirement.maximumExclusive() != null
                && version.compareTo(requirement.maximumExclusive()) >= 0) {
            return VersionCheckResult.AT_OR_ABOVE_EXCLUSIVE_MAXIMUM;
        }
        return VersionCheckResult.SATISFIES_REQUIREMENT;
    }
}
