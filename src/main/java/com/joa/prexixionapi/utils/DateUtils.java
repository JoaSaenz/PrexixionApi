package com.joa.prexixionapi.utils;

import org.joda.time.LocalDate;

public class DateUtils {

    public static final java.time.ZoneId ZONE_LIMA = java.time.ZoneId.of("America/Lima");

    public static String getToday() {
        return java.time.LocalDate.now(ZONE_LIMA).toString();
    }

    public static String getToday(String formato) {
        return java.time.LocalDate.now(ZONE_LIMA)
                .format(java.time.format.DateTimeFormatter.ofPattern(formato));
    }

    public static String getTodayString() {
        return getToday();
    }

    public static String getYesterday() {
        return java.time.LocalDate.now(ZONE_LIMA).minusDays(1).toString();
    }

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

    public static long getDifferenceInDays(String someday) {
        if (someday == null || someday.isEmpty())
            return 0;
        try {
            LocalDate target = LocalDate.parse(someday);
            LocalDate today = LocalDate.now();
            return org.joda.time.Days.daysBetween(target, today).getDays();
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getNameStMonth(String month) {
        switch (month) {
            case "01":
                return "Ene";
            case "02":
                return "Feb";
            case "03":
                return "Mar";
            case "04":
                return "Abr";
            case "05":
                return "May";
            case "06":
                return "Jun";
            case "07":
                return "Jul";
            case "08":
                return "Ago";
            case "09":
                return "Sep";
            case "10":
                return "Oct";
            case "11":
                return "Nov";
            case "12":
                return "Dic";
            default:
                return "";
        }
    }
}
