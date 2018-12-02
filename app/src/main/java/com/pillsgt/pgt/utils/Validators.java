package com.pillsgt.pgt.utils;

import com.pillsgt.pgt.models.PillRule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Validators {

    public boolean validatePillRuleDates(PillRule pillRule) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date curDate = calendar.getTime();

        SimpleDateFormat sdFormat = new SimpleDateFormat(Utils.dateTimePatternDb );
        sdFormat.setTimeZone( TimeZone.getTimeZone("GMT") );

        try {
            Date startDateParsed = sdFormat.parse( pillRule.getStart_date() );
            if (!curDate.after(startDateParsed)) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            Date endDateParsed = sdFormat.parse( pillRule.getStart_date() );
            if (curDate.after(endDateParsed)) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }
}
