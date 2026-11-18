package com.planetstudios.planetlib.version;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parsed numeric version with optional semantic prerelease/build metadata.
 * Supports versions such as 1.2, 1.2.3, v1.2.3-beta.1+build.7.
 */
public record ModVersion(List<Integer> components, String prerelease, String buildMetadata)
        implements Comparable<ModVersion> {
    private static final Pattern VERSION = Pattern.compile(
            "^[vV]?(\\d+(?:\\.\\d+)*)(?:-([0-9A-Za-z.-]+))?(?:\\+([0-9A-Za-z.-]+))?$");

    public ModVersion {
        components = List.copyOf(Objects.requireNonNull(components, "components"));
        if (components.isEmpty() || components.stream().anyMatch(n -> n == null || n < 0)) {
            throw new IllegalArgumentException("Version components must be non-negative integers");
        }
        prerelease = prerelease == null ? "" : prerelease;
        buildMetadata = buildMetadata == null ? "" : buildMetadata;
    }

    public static ModVersion parse(String value) {
        Objects.requireNonNull(value, "value");
        Matcher matcher = VERSION.matcher(value.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Unsupported version format: " + value);
        }
        List<Integer> parts = new ArrayList<>();
        for (String part : matcher.group(1).split("\\.")) {
            try {
                parts.add(Integer.parseInt(part));
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Version component is too large: " + part, exception);
            }
        }
        return new ModVersion(parts, matcher.group(2), matcher.group(3));
    }

    @Override
    public int compareTo(ModVersion other) {
        Objects.requireNonNull(other, "other");
        int count = Math.max(components.size(), other.components.size());
        for (int i = 0; i < count; i++) {
            int left = i < components.size() ? components.get(i) : 0;
            int right = i < other.components.size() ? other.components.get(i) : 0;
            int comparison = Integer.compare(left, right);
            if (comparison != 0) return comparison;
        }
        if (prerelease.isEmpty()) return other.prerelease.isEmpty() ? 0 : 1;
        if (other.prerelease.isEmpty()) return -1;
        return comparePrerelease(prerelease, other.prerelease);
    }

    private static int comparePrerelease(String left, String right) {
        String[] a = left.split("\\.");
        String[] b = right.split("\\.");
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            boolean an = a[i].matches("\\d+");
            boolean bn = b[i].matches("\\d+");
            int result;
            if (an && bn) result = compareNumericIdentifiers(a[i], b[i]);
            else if (an != bn) result = an ? -1 : 1;
            else result = a[i].compareTo(b[i]);
            if (result != 0) return result;
        }
        return Integer.compare(a.length, b.length);
    }

    private static int compareNumericIdentifiers(String a, String b) {
        String left = a.replaceFirst("^0+(?!$)", "");
        String right = b.replaceFirst("^0+(?!$)", "");
        int length = Integer.compare(left.length(), right.length());
        return length != 0 ? length : left.compareTo(right);
    }
}
