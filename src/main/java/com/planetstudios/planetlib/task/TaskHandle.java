package com.planetstudios.planetlib.task;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Observable lifecycle handle for a submitted task.
 *
 * <p>Cancellation only prevents work that has not started. Running tasks are
 * never interrupted; task code that needs cooperative cancellation should
 * use its own application-level signal.</p>
 */
public final class TaskHandle {
    private final AtomicReference<TaskStatus> status =
            new AtomicReference<>(TaskStatus.QUEUED);
    private final CompletableFuture<TaskStatus> completion = new CompletableFuture<>();
    private volatile Throwable failure;

    TaskHandle() {
    }

    public TaskStatus status() {
        return status.get();
    }

    public boolean isDone() {
        TaskStatus current = status();
        return current == TaskStatus.SUCCEEDED || current == TaskStatus.FAILED
                || current == TaskStatus.CANCELLED;
    }

    public Optional<Throwable> failure() {
        return Optional.ofNullable(failure);
    }

    /** A read-only completion stage that resolves to the terminal task state. */
    public CompletionStage<TaskStatus> completion() {
        return completion.minimalCompletionStage();
    }

    /** Cancels a task only while it is still queued; never interrupts an executor thread. */
    public boolean cancel() {
        if (!status.compareAndSet(TaskStatus.QUEUED, TaskStatus.CANCELLED)) {
            return false;
        }
        completion.complete(TaskStatus.CANCELLED);
        return true;
    }

    boolean begin() {
        return status.compareAndSet(TaskStatus.QUEUED, TaskStatus.RUNNING);
    }

    void succeed() {
        status.set(TaskStatus.SUCCEEDED);
        completion.complete(TaskStatus.SUCCEEDED);
    }

    void fail(Throwable cause) {
        failure = cause;
        status.set(TaskStatus.FAILED);
        completion.complete(TaskStatus.FAILED);
    }
}
