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

    public static List<String> parseCsv(String csv) {
        if (csv == null || csv.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    public static List<Integer> parseCsvInt(String csv) {
        if (csv == null || csv.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(java.util.stream.Collectors.toList());
    }
}
