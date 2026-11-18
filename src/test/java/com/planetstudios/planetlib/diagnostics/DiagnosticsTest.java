package com.planetstudios.planetlib.diagnostics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiagnosticsTest {
    @Test
    void retainsNewestEventsWithinConfiguredCapacity() {
        Diagnostics diagnostics = new Diagnostics(2);
        diagnostics.record(DiagnosticLevel.INFO, "planet_lib", "startup", "Started");
        diagnostics.record(DiagnosticLevel.WARNING, "planet_lib", "compatibility", "Optional API missing");
        diagnostics.record(DiagnosticLevel.ERROR, "planet_lib", "renderer", "Renderer initialization failed");

        DiagnosticsSnapshot snapshot = diagnostics.snapshot();
        assertEquals(2, snapshot.events().size());
        assertEquals("compatibility", snapshot.events().get(0).code());
        assertEquals("renderer", snapshot.events().get(1).code());
        assertEquals(3, snapshot.totalRecorded());
        assertEquals(1, snapshot.droppedFromBuffer());
        assertEquals(1L, snapshot.countsByLevel().get(DiagnosticLevel.INFO));
        assertEquals(1L, snapshot.countsByLevel().get(DiagnosticLevel.ERROR));
    }

    @Test
    void recordsCallerMeasuredDurations() {
        Diagnostics diagnostics = new Diagnostics(4);
        diagnostics.recordDuration(DiagnosticLevel.DEBUG, "planet_renderer",
                "frame_prepare", "Frame preparation", 125_000L);

        DiagnosticEvent event = diagnostics.snapshot().events().get(0);
        assertTrue(event.hasDuration());
        assertEquals(125_000L, event.durationNanos());
    }

    @Test
    void rejectsInvalidInputAndKeepsSnapshotsImmutable() {
        Diagnostics diagnostics = new Diagnostics(1);
        assertThrows(IllegalArgumentException.class, () -> new Diagnostics(0));
        assertThrows(IllegalArgumentException.class,
                () -> diagnostics.record(DiagnosticLevel.INFO, "Planet Mod", "startup", "Started"));
        diagnostics.record(DiagnosticLevel.INFO, "planet_lib", "startup", "Started");
        assertThrows(UnsupportedOperationException.class,
                () -> diagnostics.snapshot().events().clear());
    }

    @Test
    void clearResetsBufferAndCounters() {
        Diagnostics diagnostics = new Diagnostics(2);
        diagnostics.record(DiagnosticLevel.ERROR, "planet_lib", "test", "Failure");
        diagnostics.clear();

        DiagnosticsSnapshot snapshot = diagnostics.snapshot();
        assertTrue(snapshot.events().isEmpty());
        assertEquals(0, snapshot.totalRecorded());
        assertEquals(0, snapshot.droppedFromBuffer());
        assertEquals(0L, snapshot.countsByLevel().get(DiagnosticLevel.ERROR));
    }
}
