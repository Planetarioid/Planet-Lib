package com.planetstudios.planetlib.resource;

import java.util.Objects;

/** A stable, namespaced logical identifier for a mod-contributed resource. */
public record ResourceId(String namespace, String path) implements Comparable<ResourceId> {
    public ResourceId {
        namespace = Objects.requireNonNull(namespace, "namespace");
        path = Objects.requireNonNull(path, "path");
        if (!namespace.matches("[a-z0-9_.-]+")) {
            throw new IllegalArgumentException("namespace must be a lowercase identifier");
        }
        if (path.isBlank() || path.startsWith("/") || path.endsWith("/")
                || path.contains("//") || path.contains("\\")
                || !path.matches("[a-z0-9/._-]+")) {
            throw new IllegalArgumentException("path must be a safe lowercase resource path");
        }
        for (String segment : path.split("/")) {
            if (segment.equals(".") || segment.equals("..")) {
                throw new IllegalArgumentException("path must not contain traversal segments");
            }
        }
    }

    public static ResourceId parse(String value) {
        Objects.requireNonNull(value, "value");
        int separator = value.indexOf(':');
        if (separator <= 0 || separator != value.lastIndexOf(':') || separator == value.length() - 1) {
            throw new IllegalArgumentException("Resource ID must use namespace:path format");
        }
        return new ResourceId(value.substring(0, separator), value.substring(separator + 1));
    }

    @Override
    public int compareTo(ResourceId other) {
        int namespaceOrder = namespace.compareTo(other.namespace);
        return namespaceOrder != 0 ? namespaceOrder : path.compareTo(other.path);
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }
}
