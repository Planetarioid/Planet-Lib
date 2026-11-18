package com.planetstudios.planetlib.diagnostics;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Immutable point-in-time view of the bounded diagnostic event buffer. */
public record DiagnosticsSnapshot(
        List<DiagnosticEvent> events,
        int capacity,
        long totalRecorded,
        long droppedFromBuffer,
        Map<DiagnosticLevel, Long> countsByLevel
) {
    public DiagnosticsSnapshot {
        events = List.copyOf(Objects.requireNonNull(events, "events"));
        if (capacity < 1) throw new IllegalArgumentException("capacity must be positive");
        if (totalRecorded < 0 || droppedFromBuffer < 0) {
            throw new IllegalArgumentException("record counts cannot be negative");
        }
        countsByLevel = Map.copyOf(Objects.requireNonNull(countsByLevel, "countsByLevel"));
    }
}
