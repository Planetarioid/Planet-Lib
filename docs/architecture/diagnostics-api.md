# Diagnostics API

The Diagnostics API is a small, thread-safe, in-memory event recorder shared by Planet Studios mods.

## Events

Each immutable event contains a timestamp, severity, source mod ID, stable event code, message, and optional caller-measured duration in nanoseconds. Severity levels are DEBUG, INFO, WARNING, and ERROR.

## Buffer and snapshots

The shared recorder retains at most 256 events. When full, it evicts the oldest event and increments a dropped-from-buffer count. Snapshots are immutable and include retained events, total recorded events, evictions, and counts by severity. Integrations may construct isolated recorders with a different positive capacity.

## Performance

Recording is explicit and does not automatically poll game state, hook rendering, profile every frame, or schedule background work. Duration measurements are supplied by the caller, so the API does not add timing overhead unless an integration chooses to measure something. The recorder uses a short synchronized critical section and bounded memory.

## Privacy and boundaries

Events remain in memory. The API does not write logs to disk, upload telemetry, collect player data, or automatically include exception stack traces. Integrations should keep messages concise and avoid secrets, personal information, or large payloads. Clearing diagnostics is explicit.
