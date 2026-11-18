package com.planetstudios.planetlib.diagnostics;

import java.time.Instant;
import java.util.Objects;

/** Immutable diagnostic event emitted by a Planet Studios integration. */
public record DiagnosticEvent(
        Instant timestamp,
        DiagnosticLevel level,
        String sourceModId,
        String code,
        String message,
        long durationNanos
) {
    public DiagnosticEvent {
        timestamp = Objects.requireNonNull(timestamp, "timestamp");
        level = Objects.requireNonNull(level, "level");
        sourceModId = validate(sourceModId, "sourceModId");
        code = validate(code, "code");
        message = Objects.requireNonNull(message, "message").trim();
        if (message.isBlank()) throw new IllegalArgumentException("message cannot be blank");
        if (durationNanos < -1) throw new IllegalArgumentException("durationNanos must be -1 or non-negative");
    }

    private static String validate(String value, String field) {
        String checked = Objects.requireNonNull(value, field).trim();
        if (!checked.matches("[a-z0-9_.-]+") || checked.equals(".") || checked.equals("..")) {
            throw new IllegalArgumentException(field + " must use lowercase letters, digits, _, . or -");
        }
        return checked;
    }

    public boolean hasDuration() {
        return durationNanos >= 0;
    }
}
