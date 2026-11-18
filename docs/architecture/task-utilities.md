# Task Utilities

## Purpose

Task Utilities provide a small, executor-neutral way for Planet Studios mods to dispatch work and observe its lifecycle. The caller supplies the executor, so the library does not create threads or make assumptions about Minecraft's client/server threading model.

## API

- `TaskAction`: task body that may throw an ordinary exception.
- `TaskStatus`: queued, running, succeeded, failed, or cancelled.
- `TaskHandle`: observes status, exposes a read-only completion stage, reports a failure, and supports cancellation before execution begins.
- `TaskDispatcher.submit(executor, action)`: dispatches exactly once through the supplied `Executor`.

## Threading and cancellation rules

The caller is responsible for choosing an executor appropriate to the work. Use the relevant Fabric/Minecraft executor for game-state operations; use a worker executor only for work that is safe off-thread. Planet Lib does not create or own executors, schedule timers, or silently move work between threads.

Cancellation succeeds only while a task remains queued. A running task is never interrupted by the handle. This avoids unexpectedly interrupting shared Minecraft/Fabric executor threads. Tasks requiring cooperative cancellation should use an explicit application-level cancellation signal.

Ordinary task exceptions and executor rejection are captured in the handle and resolve its completion stage as failed. The API does not swallow JVM errors. No task is automatically retried, persisted, or run on a per-tick polling loop.
