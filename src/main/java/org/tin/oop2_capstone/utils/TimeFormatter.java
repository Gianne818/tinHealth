package org.tin.oop2_capstone.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class TimeFormatter {

    public static String formatTo12Hour(LocalTime time){
        return time.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }
}
