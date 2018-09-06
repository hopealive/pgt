package com.pillsgt.pgt.utils;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    public static String dbDateToViewDate(String dbDate){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(Utils.dateTimePatternView );
        String curDateFormatted = format.format(calendar.getTime());
        String startDateFormatted = curDateFormatted;

        if ( dbDate.length() > 0 ){
            try {
                java.util.Date startDateParsed = new SimpleDateFormat(Utils.dateTimePatternDb)
                    .parse( dbDate );
                startDateFormatted = format.format(startDateParsed.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("PILLS_DATETIME", e.getMessage());
            }
        }

        return startDateFormatted;
    }
}
