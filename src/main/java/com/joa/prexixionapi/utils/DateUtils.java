package com.joa.prexixionapi.utils;

import org.joda.time.LocalDate;

public class DateUtils {

    public static LocalDate stringToLocalDate(String someday) {
        if (someday == null || someday.isEmpty())
            return null;
        try {
            return LocalDate.parse(someday);
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatChange(String fechaString, String formato) {
        LocalDate date = stringToLocalDate(fechaString);
        if (date == null)
            return "";
        return date.toString(formato);
    }

    public static String getTodayString() {
        return LocalDate.now().toString("yyyy-MM-dd");
    }
}
