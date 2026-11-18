package com.planetstudios.planetlib.task;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TaskDispatcherTest {
    @Test
    void executesOnSuppliedExecutorAndCompletesSuccessfully() {
        AtomicInteger calls = new AtomicInteger();
        TaskHandle handle = TaskDispatcher.submit(Runnable::run, calls::incrementAndGet);

        assertEquals(1, calls.get());
        assertEquals(TaskStatus.SUCCEEDED, handle.status());
        assertTrue(handle.isDone());
        assertEquals(TaskStatus.SUCCEEDED, handle.completion().toCompletableFuture().join());
        assertTrue(handle.failure().isEmpty());
    }

    @Test
    void queuedTaskCanBeCancelledBeforeExecution() {
        List<Runnable> queue = new ArrayList<>();
        Executor executor = queue::add;
        AtomicInteger calls = new AtomicInteger();

        TaskHandle handle = TaskDispatcher.submit(executor, calls::incrementAndGet);
        assertEquals(TaskStatus.QUEUED, handle.status());
        assertTrue(handle.cancel());
        queue.forEach(Runnable::run);

        assertEquals(0, calls.get());
        assertEquals(TaskStatus.CANCELLED, handle.status());
        assertEquals(TaskStatus.CANCELLED, handle.completion().toCompletableFuture().join());
        assertFalse(handle.cancel());
    }

    @Test
    void capturesTaskFailures() {
        IllegalStateException expected = new IllegalStateException("task failed");
        TaskHandle handle = TaskDispatcher.submit(Runnable::run, () -> {
            throw expected;
        });

        assertEquals(TaskStatus.FAILED, handle.status());
        assertSame(expected, handle.failure().orElseThrow());
        assertEquals(TaskStatus.FAILED, handle.completion().toCompletableFuture().join());
    }

    @Test
    void capturesExecutorRejection() {
        Executor rejecting = command -> {
            throw new RejectedExecutionException("queue full");
        };
        TaskHandle handle = TaskDispatcher.submit(rejecting, () -> fail("must not run"));

        assertEquals(TaskStatus.FAILED, handle.status());
        assertInstanceOf(RejectedExecutionException.class, handle.failure().orElseThrow());
    }

    @Test
    void runningTaskCannotBeCancelledByHandle() {
        List<Runnable> queue = new ArrayList<>();
        TaskHandle[] observed = new TaskHandle[1];
        AtomicInteger calls = new AtomicInteger();
        observed[0] = TaskDispatcher.submit(queue::add, () -> {
            calls.incrementAndGet();
            assertFalse(observed[0].cancel());
        });

        queue.forEach(Runnable::run);
        assertEquals(1, calls.get());
        assertEquals(TaskStatus.SUCCEEDED, observed[0].status());
    }

    @Test
    void rejectsNullArguments() {
        assertThrows(NullPointerException.class, () -> TaskDispatcher.submit(null, () -> { }));
        assertThrows(NullPointerException.class, () -> TaskDispatcher.submit(Runnable::run, null));
    }
}
