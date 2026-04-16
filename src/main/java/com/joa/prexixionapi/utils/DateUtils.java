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

    public static long getDifferenceInDays(String someday) {
        if (someday == null || someday.isEmpty())
            return 0;
        try {
            LocalDate today = LocalDate.now();
            LocalDate target = LocalDate.parse(someday);
            return org.joda.time.Days.daysBetween(today, target).getDays();
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getNameStMonth(String month) {
        switch (month) {
            case "01": return "Ene";
            case "02": return "Feb";
            case "03": return "Mar";
            case "04": return "Abr";
            case "05": return "May";
            case "06": return "Jun";
            case "07": return "Jul";
            case "08": return "Ago";
            case "09": return "Sep";
            case "10": return "Oct";
            case "11": return "Nov";
            case "12": return "Dic";
            default: return "";
        }
    }
}
