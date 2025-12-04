package com.roxiun.mellow.util;

import java.util.UUID;
import java.util.regex.Pattern;

public class UUIDUtils {

    private static final Pattern UUID_PATTERN = Pattern.compile(
        "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{12})"
    );

    /**
     * Converts a UUID string (with or without dashes) to a UUID object.
     * If the UUID string doesn't contain dashes, it will be formatted to include them.
     *
     * @param uuidString The UUID string to convert
     * @return The UUID object
     */
    public static UUID fromString(String uuidString) {
        if (!uuidString.contains("-")) {
            uuidString = uuidString.replaceFirst(
                "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{12})",
                "$1-$2-$3-$4-$5"
            );
        }
        return UUID.fromString(uuidString);
    }
}
