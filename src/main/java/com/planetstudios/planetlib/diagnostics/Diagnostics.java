package com.planetstudios.planetlib.diagnostics;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Thread-safe, bounded in-memory diagnostics recorder.
 *
 * <p>Recording is explicit. Events are not written to disk, uploaded, or
 * printed automatically. The shared instance uses a fixed small capacity;
 * integrations can create isolated recorders for tests or specialized use.</p>
 */
public final class Diagnostics {
    public static final int DEFAULT_CAPACITY = 256;
    private static final Diagnostics SHARED = new Diagnostics(DEFAULT_CAPACITY);

    private final int capacity;
    private final ArrayDeque<DiagnosticEvent> events;
    private long totalRecorded;
    private long droppedFromBuffer;
    private final EnumMap<DiagnosticLevel, Long> countsByLevel =
            new EnumMap<>(DiagnosticLevel.class);

    public Diagnostics(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("capacity must be positive");
        this.capacity = capacity;
        this.events = new ArrayDeque<>(capacity);
        for (DiagnosticLevel level : DiagnosticLevel.values()) {
            countsByLevel.put(level, 0L);
        }
    }

    /** Returns the shared in-memory recorder. */
    public static Diagnostics shared() {
        return SHARED;
    }

    public void record(DiagnosticLevel level, String sourceModId, String code, String message) {
        append(new DiagnosticEvent(Instant.now(), level, sourceModId, code, message, -1));
    }

    /** Records a caller-measured duration without imposing timing work on the recorder. */
    public void recordDuration(
            DiagnosticLevel level, String sourceModId, String code, String message, long durationNanos) {
        if (durationNanos < 0) throw new IllegalArgumentException("durationNanos cannot be negative");
        append(new DiagnosticEvent(Instant.now(), level, sourceModId, code, message, durationNanos));
    }

    public synchronized DiagnosticsSnapshot snapshot() {
        Map<DiagnosticLevel, Long> counts = new EnumMap<>(countsByLevel);
        return new DiagnosticsSnapshot(
                new ArrayList<>(events), capacity, totalRecorded, droppedFromBuffer, counts);
    }

    /** Clears retained events and counters. Intended for explicit diagnostics controls and tests. */
    public synchronized void clear() {
        events.clear();
        totalRecorded = 0;
        droppedFromBuffer = 0;
        for (DiagnosticLevel level : DiagnosticLevel.values()) {
            countsByLevel.put(level, 0L);
        }
    }

    private synchronized void append(DiagnosticEvent event) {
        if (events.size() == capacity) {
            events.removeFirst();
            droppedFromBuffer++;
        }
        events.addLast(event);
        totalRecorded++;
        countsByLevel.compute(event.level(), (level, count) -> count + 1);
    }
}
