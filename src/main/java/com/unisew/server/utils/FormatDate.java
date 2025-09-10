package com.unisew.server.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FormatDate {
    public static LocalDateTime dateToTime(LocalDate date) {
        return (date == null) ? null : date.atStartOfDay(); // 00:00:00
    }
    public static LocalDate timeToDate(LocalDateTime dateTime) {
        return (dateTime == null) ? null : dateTime.toLocalDate();
    }
}
