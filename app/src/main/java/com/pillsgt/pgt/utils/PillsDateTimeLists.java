package com.pillsgt.pgt.utils;

import android.util.Log;

import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.models.PillTimeRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class PillsDateTimeLists {
    /**
     * ###############
     * TIME BLOCK
     * ###############
     */

    //Breakfeast in 8.00; lunch 11.00; dinner 13.00; snack 15.00; supper 18.00
    public static final List<String> defaultEatingFields = Arrays.asList(
            "breakfast",
            "lunch",
            "dinner",
            "snack",
            "supper"
    );

    public static final List<String> defaultEatingTimes = Arrays.asList(
            "8:00",
            "11:00",
            "13:00",
            "15:00",
            "18:00"
    );

    /*
    * Eating duration in minutes
     */
    public static final int eatDuration(){
        //todo: get from database
        return 60;
    }

    public static List<String> getTimeList(PillRule pillRule){
        List<String> list = new ArrayList<>();

//        list = PillsDateTimeLists.defaultEatingTimes; //todo: use in future

        //check if pill time rules exists

//        List<PillTimeRule> pillTimeRules = localDatabase.localDAO().loadTimeRuleByRuleId(pillRule.getPill_id());
//        for ( PillTimeRule pillTimeRule : pillTimeRules){
//            list.add(pillTimeRule.getAlarm_at());
//        }

        //if pill time rules doesn't exists set defaults


        //set current date with start time startTime:00
        int startTime = 8;//todo: get from db
        int endTime = 22;//todo: get from db

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, startTime);
        cal.set(Calendar.MINUTE, 0);

        int cronTypeKoef = 0;
        if (pillRule.getCron_type() == 4 ){
            cronTypeKoef = 0;
        } else {
            cronTypeKoef = pillRule.getCron_type() -1;
        }
        int koef = (eatDuration()/2)*cronTypeKoef;//in minutes. While eating = duration / 2

        switch (pillRule.getCron_interval()){
            case 1: //once_a_day - morning
                cal.add( Calendar.MINUTE, koef );
                list.add( String.format("%1$tH:%1$tM", cal) );
                break;
            case 2://twice_a_day
            case 3://three_times_a_day
            case 4://four_times_a_day
            case 5://five_times_a_day
            case 6://six_times_a_day
                int timeDiff = (endTime - startTime)/pillRule.getCron_interval();//in hours

                int ii = 0;
                while ( cal.get(Calendar.HOUR_OF_DAY) >= startTime
                        && cal.get(Calendar.HOUR_OF_DAY) < endTime
                        && cal.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                        && ii < pillRule.getCron_interval()
                        ) {
                    Calendar bCal = Calendar.getInstance();
                    bCal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) );
                    bCal.set(Calendar.MINUTE, 0);//set 00
                    bCal.add(Calendar.MINUTE, koef);//add minutes, can be more 1 hour

                    list.add(String.format("%1$tH:%1$tM", bCal) );
                    cal.add(Calendar.HOUR, timeDiff);
                    ++ii;
                }
                break;
            case 21://every_30_minutes
                while ( cal.get(Calendar.HOUR_OF_DAY) >= startTime
                        && cal.get(Calendar.HOUR_OF_DAY) < endTime){
                    cal.add(Calendar.MINUTE, 30);
                    list.add( String.format("%1$tH:%1$tM", cal) );
                }
                break;
            case 22://every_1_hour
            case 23://every_2_hours
            case 24://every_3_hours
            case 25://every_4_hours
            case 26://every_5_hours
            case 27: //every_6_hours
                //every pillRule.getCron_interval() - 21
                int cronInterval = pillRule.getCron_interval() - 21;
                for ( int i = startTime + cronInterval; i <= endTime; i = i + cronInterval  ){
                    Calendar bCal = Calendar.getInstance();
                    bCal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) );
                    bCal.set(Calendar.MINUTE, 0);//set 00
                    bCal.add(Calendar.MINUTE, koef);//add minutes, can be more 1 hour

                    cal.set(Calendar.HOUR_OF_DAY, i);
                    list.add( String.format("%1$tH:%1$tM", bCal) );
                }
                break;
            case 100: //others
                list.add( "12:00" );
                break;
            }

            if ( list.isEmpty()){
                list.add( String.format("%1$tH:%1$tM", cal) );
            }
        return list;
    }

}
