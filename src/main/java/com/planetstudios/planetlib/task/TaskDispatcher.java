package com.planetstudios.planetlib.task;

import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * Small executor adapter for shared mod tasks.
 *
 * <p>Planet Lib does not create threads, own an executor, schedule timers, or
 * decide which thread is appropriate. Callers must supply a suitable executor
 * (for example, a Fabric/Minecraft-provided executor for game-thread work).
 * Tasks are dispatched exactly once and expose a lifecycle handle.</p>
 */
public final class TaskDispatcher {
    private TaskDispatcher() {
    }

    public static TaskHandle submit(Executor executor, TaskAction action) {
        Objects.requireNonNull(executor, "executor");
        Objects.requireNonNull(action, "action");

        TaskHandle handle = new TaskHandle();
        try {
            executor.execute(() -> {
                if (!handle.begin()) {
                    return;
                }
                try {
                    action.run();
                    handle.succeed();
                } catch (Exception exception) {
                    handle.fail(exception);
                }
            });
        } catch (RuntimeException rejection) {
            handle.fail(rejection);
        }
        return handle;
    }
}
