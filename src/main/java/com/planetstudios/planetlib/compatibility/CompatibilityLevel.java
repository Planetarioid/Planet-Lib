package com.planetstudios.planetlib.compatibility;

/**
 * Describes the strength and nature of a declared compatibility relationship.
 *
 * <p>These values describe evidence/integration status, not an automatic
 * guarantee that arbitrary mod combinations are safe.</p>
 */
public enum CompatibilityLevel {
    /** A direct integration implemented through a supported public API. */
    NATIVE_INTEGRATION,
    /** The exact combination has been exercised in a documented test setup. */
    TESTED_COEXISTENCE,
    /** Coexistence is expected only under stated conditions or limitations. */
    CONDITIONAL,
    /** The mod is identified, but no behavioral compatibility claim is made. */
    DETECT_ONLY,
    /** A known incompatibility has been documented for this relationship. */
    INCOMPATIBLE
}
