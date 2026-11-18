# Planet Lib API Usage

Planet Lib targets Fabric, Minecraft Java 1.21.11, and Java 21. Add Planet Lib as a required dependency in a consuming mod's `fabric.mod.json` before using its APIs.

## Initialization

Register metadata during the consuming mod's normal initialization, not from a tick, render, or input callback.

```java
package com.examplemod;

import com.planetstudios.planetlib.feature.FeatureDescriptor;
import com.planetstudios.planetlib.feature.FeatureId;
import com.planetstudios.planetlib.feature.FeatureRegistry;
import net.fabricmc.api.ModInitializer;

import java.util.Set;

public final class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "example_mod";

    @Override
    public void onInitialize() {
        FeatureRegistry.getInstance().register(new FeatureDescriptor(
                new FeatureId(MOD_ID, "example-feature"),
                "Example Feature",
                "Example feature metadata.",
                "general",
                Set.of("example")
        ));
    }
}
```

## Shared registries

- `ModRegistry.snapshot()` reads the latest loaded-mod snapshot. Planet Lib refreshes it during its initializer.
- `FeatureRegistry.getInstance()` registers feature metadata.
- `CapabilityRegistry.getInstance()` registers capabilities. The owner mod must be loaded.
- `CompatibilityRegistry.getInstance()` registers explicit compatibility declarations. The declaring mod must be loaded.
- `ResourceRegistry.getInstance()` registers logical resource metadata. The owner mod must be loaded.
- `ConfigManager.getInstance()` registers per-mod JSON configuration handles.
- `Diagnostics.shared()` records explicit bounded in-memory diagnostics.
- `FeaturePriorityManager` is constructed with the feature registry used by the integration and resolves competing implementations.
- `TaskDispatcher.submit(executor, action)` dispatches work on an executor selected by the caller.
- `InputActionRegistry` is an explicitly constructed metadata registry. It does not connect physical input by itself.

## Threading

The registries are intended for initialization-time registration and immutable snapshot reads. `ConfigHandle` returns a mutable configuration object; callers must coordinate concurrent access to that object.

For tasks, always choose an executor appropriate to the work. Minecraft world and client state must only be accessed on the correct Minecraft/Fabric thread. Planet Lib deliberately does not create worker threads or move work between executors.

## Optional Planet Essentials integration

Use `PlanetLib.getEssentialsBridge()` to inspect the bridge status. When available, submit immutable integration metadata through `registerIntegration`. Planet Essentials is optional and is not a hard dependency.

## API boundaries

Planet Lib supplies shared infrastructure and metadata contracts. It does not load arbitrary assets, rewrite another mod's configuration, activate feature providers, synthesize input, or claim compatibility without an explicit declaration and supporting evidence.
