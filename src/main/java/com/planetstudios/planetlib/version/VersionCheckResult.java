package com.planetstudios.planetlib.version;

/** Outcome of checking a loaded mod against a declared version requirement. */
public enum VersionCheckResult {
    MOD_NOT_LOADED,
    VERSION_UNPARSEABLE,
    BELOW_MINIMUM,
    AT_OR_ABOVE_EXCLUSIVE_MAXIMUM,
    SATISFIES_REQUIREMENT
}
