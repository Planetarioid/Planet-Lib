package com.planetstudios.planetlib.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void createsDefaultsAndPersistsChanges() throws IOException {
        ConfigManager manager = new ConfigManager(temporaryDirectory);
        ConfigManager.ConfigHandle<TestConfig> handle =
                manager.register("planet_test", "client", TestConfig.class, TestConfig::new);

        assertTrue(Files.exists(handle.getPath()));
        assertEquals(8, handle.get().volume);

        handle.get().volume = 3;
        handle.save();

        ConfigManager secondManager = new ConfigManager(temporaryDirectory);
        ConfigManager.ConfigHandle<TestConfig> reloaded =
                secondManager.register("planet_test", "client", TestConfig.class, TestConfig::new);
        assertEquals(3, reloaded.get().volume);
    }

    @Test
    void reloadReadsExternalChanges() throws IOException {
        ConfigManager manager = new ConfigManager(temporaryDirectory);
        ConfigManager.ConfigHandle<TestConfig> handle =
                manager.register("planet_test", "client", TestConfig.class, TestConfig::new);

        Files.writeString(handle.getPath(), "{\"volume\": 2}");
        handle.reload();

        assertEquals(2, handle.get().volume);
    }

    @Test
    void malformedFileIsPreserved() throws IOException {
        Path directory = temporaryDirectory.resolve("planet_test");
        Files.createDirectories(directory);
        Path file = directory.resolve("client.json");
        String malformed = "{ definitely not json";
        Files.writeString(file, malformed);

        ConfigManager manager = new ConfigManager(temporaryDirectory);
        assertThrows(IOException.class,
                () -> manager.register("planet_test", "client", TestConfig.class, TestConfig::new));
        assertEquals(malformed, Files.readString(file));
    }

    @Test
    void rejectsPathTraversalAndDuplicateRegistration() throws IOException {
        ConfigManager manager = new ConfigManager(temporaryDirectory);
        assertThrows(IllegalArgumentException.class,
                () -> manager.register("../outside", "client", TestConfig.class, TestConfig::new));

        manager.register("planet_test", "client", TestConfig.class, TestConfig::new);
        assertThrows(IllegalArgumentException.class,
                () -> manager.register("planet_test", "client", TestConfig.class, TestConfig::new));
    }

    static final class TestConfig {
        int volume = 8;
    }
}
