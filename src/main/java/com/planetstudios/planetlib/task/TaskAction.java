package com.planetstudios.planetlib.task;

/** A task action that may report an ordinary checked exception. */
@FunctionalInterface
public interface TaskAction {
    void run() throws Exception;
}
