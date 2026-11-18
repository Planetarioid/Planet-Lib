package com.planetstudios.planetlib.registry;

import java.util.Collection;

/**
 * Shared rules for identifying first-party Planet Studios mod IDs.
 */
final class ModIdentity {
    static final String MOD_ID_PREFIX = "planet_";

    private ModIdentity() {
    }

    static boolean isPlanetMod(String id, Collection<String> providedIds) {
        if (hasPlanetPrefix(id)) {
            return true;
        }
        return providedIds != null && providedIds.stream().anyMatch(ModIdentity::hasPlanetPrefix);
    }

    private static boolean hasPlanetPrefix(String id) {
        return id != null && id.startsWith(MOD_ID_PREFIX);
    }
}
