package com.planetstudios.planetlib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Registers and owns typed JSON configuration files for individual mods.
 *
 * <p>Configuration is loaded or written only during explicit registration,
 * reload, or save calls. This class performs no background or tick-based work.</p>
 */
public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private final Path rootDirectory;
    private final Map<ConfigKey, ConfigHandle<?>> handles = new HashMap<>();

    /**
     * Creates a manager rooted at the supplied Planet Lib configuration directory.
     * This constructor is public to support isolated tests and custom environments.
     */
    public ConfigManager(Path rootDirectory) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory, "rootDirectory")
                .toAbsolutePath()
                .normalize();
    }

    /**
     * Returns the process-wide manager using Fabric's config directory.
     */
    public static ConfigManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Registers a typed configuration for a mod.
     *
     * <p>The resulting file is stored at
     * {@code config/planetlib/<modId>/<fileName>.json}. If it does not exist,
     * the supplied defaults are written once. Existing malformed files are
     * preserved and cause registration to fail rather than being overwritten.</p>
     *
     * @throws IOException if the file cannot be read or initialized
     * @throws IllegalArgumentException if an identifier is invalid or already registered
     */
    public synchronized <T> ConfigHandle<T> register(
            String modId,
            String fileName,
            Class<T> configType,
            Supplier<T> defaults
    ) throws IOException {
        validateSegment(modId, "modId");
        validateSegment(fileName, "fileName");
        Objects.requireNonNull(configType, "configType");
        Objects.requireNonNull(defaults, "defaults");

        ConfigKey key = new ConfigKey(modId, fileName);
        if (handles.containsKey(key)) {
            throw new IllegalArgumentException("Configuration already registered: " + modId + "/" + fileName);
        }

        Path file = rootDirectory.resolve(modId).resolve(fileName + ".json").normalize();
        if (!file.startsWith(rootDirectory)) {
            throw new IllegalArgumentException("Configuration path escapes the configured root");
        }

        T value;
        if (Files.exists(file)) {
            value = read(file, configType);
        } else {
            value = Objects.requireNonNull(defaults.get(), "Default configuration supplier returned null");
            write(file, value);
        }

        ConfigHandle<T> handle = new ConfigHandle<>(file, configType, defaults, value);
        handles.put(key, handle);
        return handle;
    }

    private static <T> T read(Path file, Class<T> configType) throws IOException {
        try (var reader = Files.newBufferedReader(file)) {
            T value = GSON.fromJson(reader, configType);
            if (value == null) {
                throw new IOException("Configuration file contains no JSON object: " + file);
            }
            return value;
        } catch (com.google.gson.JsonParseException | IllegalStateException exception) {
            throw new IOException("Could not parse configuration file; original file was left untouched: " + file,
                    exception);
        }
    }

    private static <T> void write(Path file, T value) throws IOException {
        Path parent = file.getParent();
        Files.createDirectories(parent);
        Path temporary = Files.createTempFile(parent, file.getFileName().toString(), ".tmp");
        try {
            try (var writer = Files.newBufferedWriter(temporary)) {
                GSON.toJson(value, writer);
            }
            try {
                Files.move(temporary, file,
                        java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (java.nio.file.AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, file, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static void validateSegment(String value, String name) {
        if (value == null || !value.matches("[a-z0-9_.-]+") || value.equals(".") || value.equals("..")) {
            throw new IllegalArgumentException(name + " must contain only lowercase letters, digits, '.', '_' or '-'");
        }
    }

    private record ConfigKey(String modId, String fileName) {}

    private static final class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager(
                FabricLoader.getInstance().getConfigDir().resolve("planetlib"));
    }

    /**
     * A typed view of one registered configuration file.
     */
    public static final class ConfigHandle<T> {
        private final Path file;
        private final Class<T> configType;
        private final Supplier<T> defaults;
        private T value;

        private ConfigHandle(Path file, Class<T> configType, Supplier<T> defaults, T value) {
            this.file = file;
            this.configType = configType;
            this.defaults = defaults;
            this.value = value;
        }

        /** Returns the current in-memory configuration object. */
        public synchronized T get() {
            return value;
        }

        /** Replaces the in-memory value. Call save() to persist it. */
        public synchronized void set(T value) {
            this.value = Objects.requireNonNull(value, "value");
        }

        /** Writes the current value using a temporary file and atomic replacement where supported. */
        public synchronized void save() throws IOException {
            write(file, value);
        }

        /**
         * Reloads from disk. If the file was removed, defaults are restored
         * and written to disk. A malformed file is never silently replaced.
         */
        public synchronized void reload() throws IOException {
            if (Files.exists(file)) {
                value = read(file, configType);
            } else {
                value = Objects.requireNonNull(defaults.get(), "Default configuration supplier returned null");
                write(file, value);
            }
        }

        /** Returns the configuration file location. */
        public Path getPath() {
            return file;
        }
    }
}
