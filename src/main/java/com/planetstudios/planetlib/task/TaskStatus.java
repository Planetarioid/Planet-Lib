package com.planetstudios.planetlib.task;

/** Lifecycle state of a task submitted through the Planet Lib task utilities. */
public enum TaskStatus {
    QUEUED,
    RUNNING,
    SUCCEEDED,
    FAILED,
    CANCELLED
}
