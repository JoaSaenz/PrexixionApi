package com.joa.prexixionapi.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtils {

    public static String emptyToString(String value) {
        return (value == null) ? "" : value;
    }

    public static List<String> splitOnChar(String value, String delimiter) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(value.split(delimiter));
    }

    public static String removeChars(String value, String charsToRemove) {
        if (value == null) return "";
        return value.replace(charsToRemove, "");
    }
}
