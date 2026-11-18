package com.planetstudios.planetlib.essentials;

/**
 * Service contract implemented by Planet Essentials and exposed through the
 * Fabric entrypoint key {@code planet_essentials_api}.
 *
 * <p>Planet Essentials must compile against the same major contract version.
 * This interface deliberately contains no Minecraft client classes so it can
 * be referenced safely by common code.</p>
 */
public interface PlanetEssentialsApi {
    int API_VERSION = 1;

    /** Returns the API contract version implemented by Essentials. */
    int apiVersion();

    /** Announces a Planet mod integration to Essentials. */
    void registerIntegration(PlanetIntegration integration);
}
