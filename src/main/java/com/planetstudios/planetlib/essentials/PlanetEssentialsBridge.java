package com.planetstudios.planetlib.essentials;

import net.fabricmc.loader.api.FabricLoader;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Optional, version-negotiated connection to Planet Essentials.
 *
 * <p>Essentials exposes a {@link PlanetEssentialsApi} using the Fabric
 * entrypoint key {@code planet_essentials_api}. No hard dependency or
 * reflection is used. Discovery is explicit and should happen during
 * initialization, not from a tick or render callback.</p>
 */
public final class PlanetEssentialsBridge {
    public static final String ENTRYPOINT_KEY = "planet_essentials_api";

    public enum Status {
        NOT_DISCOVERED,
        ESSENTIALS_NOT_INSTALLED,
        AVAILABLE,
        INCOMPATIBLE_API,
        DISCOVERY_FAILED
    }

    private final boolean essentialsInstalled;
    private final Status status;
    private final PlanetEssentialsApi api;
    private final String detail;

    private PlanetEssentialsBridge(boolean essentialsInstalled, Status status,
                                   PlanetEssentialsApi api, String detail) {
        this.essentialsInstalled = essentialsInstalled;
        this.status = Objects.requireNonNull(status, "status");
        this.api = api;
        this.detail = Objects.requireNonNull(detail, "detail");
    }

    /** Creates the initial state before optional integration discovery. */
    public static PlanetEssentialsBridge undiscovered() {
        return new PlanetEssentialsBridge(false, Status.NOT_DISCOVERED, null, "Discovery has not run.");
    }

    /**
     * Discovers the optional Essentials entrypoint and validates its API version.
     * A broken or incompatible integration never prevents Planet Lib startup.
     */
    public static PlanetEssentialsBridge discover() {
        FabricLoader loader = FabricLoader.getInstance();
        boolean installed = loader.isModLoaded("planet_essentials");
        if (!installed) {
            return new PlanetEssentialsBridge(false, Status.ESSENTIALS_NOT_INSTALLED,
                    null, "Planet Essentials is not installed.");
        }

        try {
            List<PlanetEssentialsApi> entrypoints =
                    loader.getEntrypoints(ENTRYPOINT_KEY, PlanetEssentialsApi.class);
            if (entrypoints.isEmpty()) {
                return new PlanetEssentialsBridge(true, Status.INCOMPATIBLE_API,
                        null, "Planet Essentials did not expose the Planet Lib API entrypoint.");
            }
            if (entrypoints.size() != 1) {
                return new PlanetEssentialsBridge(true, Status.INCOMPATIBLE_API,
                        null, "Expected exactly one Planet Essentials API entrypoint.");
            }

            PlanetEssentialsApi candidate = entrypoints.get(0);
            int version = candidate.apiVersion();
            if (version != PlanetEssentialsApi.API_VERSION) {
                return new PlanetEssentialsBridge(true, Status.INCOMPATIBLE_API,
                        null, "Unsupported Planet Essentials API version: " + version);
            }
            return new PlanetEssentialsBridge(true, Status.AVAILABLE, candidate,
                    "Planet Essentials API v" + version + " is available.");
        } catch (RuntimeException exception) {
            return new PlanetEssentialsBridge(true, Status.DISCOVERY_FAILED,
                    null, "Planet Essentials API discovery failed: " + exception.getClass().getSimpleName());
        }
    }

    public boolean isEssentialsInstalled() { return essentialsInstalled; }
    public boolean isAvailable() { return status == Status.AVAILABLE; }
    public Status status() { return status; }
    public String detail() { return detail; }

    /** Returns the negotiated API only when the bridge is available. */
    public Optional<PlanetEssentialsApi> api() {
        return Optional.ofNullable(api);
    }

    /**
     * Registers integration metadata when Essentials is available.
     *
     * @return true if Essentials accepted the registration call, false if
     *         Essentials is absent or unavailable
     */
    public boolean registerIntegration(PlanetIntegration integration) {
        Objects.requireNonNull(integration, "integration");
        if (api == null) {
            return false;
        }
        try {
            api.registerIntegration(integration);
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }
}
