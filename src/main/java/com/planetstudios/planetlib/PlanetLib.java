package com.planetstudios.planetlib;

import com.planetstudios.planetlib.registry.ModRegistry;
import com.planetstudios.planetlib.registry.ModRegistrySnapshot;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for Planet Lib.
 *
 * <p>Keep initialization lightweight. Shared services should be initialized
 * only when they are needed by a dependent Planet Studios mod.</p>
 */
public final class PlanetLib implements ModInitializer {
    public static final String MOD_ID = "planet_lib";
    public static final Logger LOGGER = LoggerFactory.getLogger("Planet Lib");

    @Override
    public void onInitialize() {
        ModRegistrySnapshot snapshot = ModRegistry.refresh();
        LOGGER.info("Planet Lib initialized. Detected {} loaded mods, including {} Planet Studios mods.",
                snapshot.getMods().size(), snapshot.getPlanetMods().size());
    }
}
