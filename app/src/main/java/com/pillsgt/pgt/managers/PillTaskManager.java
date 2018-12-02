package com.pillsgt.pgt.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pillsgt.pgt.databases.InitDatabases;
import com.pillsgt.pgt.databases.LocalDatabase;
import com.pillsgt.pgt.databases.RemoteDatabase;
import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.models.PillTask;
import com.pillsgt.pgt.models.PillTimeRule;
import com.pillsgt.pgt.models.remote.PillsUa;
import com.pillsgt.pgt.utils.Utils;
import com.pillsgt.pgt.utils.Validators;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class PillTaskManager {

    private static final String TAG = "PTManager";
    private Integer ruleId;
    private Context mContext;

    public PillTaskManager(Integer ruleId, Context context) {
        this.ruleId = ruleId;
        this.mContext = context;

        new PillTaskManager.GoPillTaskManager().execute();
    }

    private class GoPillTaskManager extends AsyncTask<Void, Void, Void> {

        private LocalDatabase localDatabase;
        private RemoteDatabase remoteDatabase;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            //todo: after ok
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            initDatabases();
            Log.d(TAG, "GoPillTaskManager doInBackground start rule id:" + ruleId);//todo: remove

            //get rule
            PillRule pillRule = localDatabase.localDAO().loadRuleById(ruleId);
            if (pillRule == null ) return null;

            PillsUa pillsUa = new PillsUa();
            if (pillRule.getPill_id() > 0){
                pillsUa = remoteDatabase.remoteDAO().loadPillsUa(pillRule.getPill_id());
            }

            //remove active task for this ruleId
            try {
                localDatabase.localDAO().deletePillTaskByRuleId(ruleId);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Error While deleting tasks. Exception " + e.getMessage());
            }

            Validators validators = new Validators();
            if ( validators.validatePillRuleDates(pillRule) ){
                return null;
            }

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdFormat = new SimpleDateFormat(Utils.dateTimePatternDb );
            sdFormat.setTimeZone( TimeZone.getTimeZone("GMT") );
            String curDateFormatted = sdFormat.format(calendar.getTime());

            List<String> pillTaskDates = getDateList(pillRule);
            List<String> pillTaskTimes = getTimeList(pillRule);
            for ( String pillTaskDate : pillTaskDates ){
                for ( String pillTaskTime : pillTaskTimes ){
Log.e("GoPillTaskManager", pillTaskDate + "| "+pillTaskDate + " " +pillTaskTime);//todo: remove
                }
            }


            //save tasks
            PillTask pillTask = new PillTask();
            pillTask.setRule_id(ruleId);
            pillTask.setTitle( pillRule.getName() );
            pillTask.setShort_title( pillsUa.getOriginal_name() );
            pillTask.setDescription( pillsUa.getDosage_form() );
            pillTask.setStatus( PillTask.STATUS_NEW );


//temporary data
Calendar nextMinDate = Calendar.getInstance();//todo: remove
nextMinDate.add(Calendar.MINUTE, 1);//todo: remove
String alertDateFormatted = sdFormat.format(nextMinDate.getTime());//todo: remove


            pillTask.setAlarm_at(alertDateFormatted);//todo: date must be normal
            pillTask.setUpdated_at(curDateFormatted);
            pillTask.setCreated_at(curDateFormatted);
            localDatabase.localDAO().addPillTask(pillTask);


            localDatabase.close();
            remoteDatabase.close();
            return null;
        }

        protected List<String> getTimeList(PillRule pillRule){
            List<String> list = new ArrayList<String>();

            List<PillTimeRule> pillTimeRules = localDatabase.localDAO().loadTimeRuleByRuleId(pillRule.getPill_id());
            for ( PillTimeRule pillTimeRule : pillTimeRules){
                list.add(pillTimeRule.getAlarm_at());
            }


//            switch (pillRule.getCron_interval()){
//                case 1: //once_a_day - morning
//                    //get breakfast time
//                    int koef = pillRule.getCron_type()-1;
//
//                    switch (pillRule.getCron_type()){
//                        case 1:
//                            list.add("08:00");//breakfast time
//                            break;
//                        case 2:
//                            list.add("08:30");//breakfast time +30 min
//                            break;
//                        case 3:
//                            list.add("09:00");//breakfast time +60 min
//                            break;
//                    }
//                    break;
//            }
//<string-array name="cron_type">
//                    1        <item>@string/before_eat</item>
//                    2        <item>@string/while_eating</item>
//                    3        <item>@string/after_eat</item>
//                    4        <item>@string/others</item>
//    </string-array>
//
//    <string-array name="cron_interval">
//                    1        <item>@string/once_a_day</item>
//                    2        <item>@string/twice_a_day</item>
//                    3        <item>@string/three_times_a_day</item>
//                    4        <item>@string/four_times_a_day</item>
//                    5        <item>@string/five_times_a_day</item>
//                    6        <item>@string/six_times_a_day</item>
//                    7        <item>@string/every_30_minutes</item>
//                    8        <item>@string/every_1_hour</item>
//                    9        <item>@string/every_2_hours</item>
//                    10        <item>@string/every_3_hours</item>
//                    11        <item>@string/every_4_hours</item>
//                    12        <item>@string/every_5_hours</item>
//                    13        <item>@string/every_6_hours</item>
//                    14        <item>@string/others</item>
//    </string-array>

            return list;
        }

        protected List<String> getDateList(PillRule pillRule){
            String startDate = pillRule.getStart_date();
            String endDate = pillRule.getEnd_date();

            ArrayList<String> dates = new ArrayList<String>();
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date dateFrom = null;
            Date dateTo = null;

            try {
                dateFrom = sdf.parse(startDate);
                dateTo = sdf.parse(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calFrom = Calendar.getInstance();
            calFrom.setTime(dateFrom);

            Calendar calTo = Calendar.getInstance();
            calTo.setTime(dateTo);

            while(!calFrom.after(calTo))
            {
                dates.add( sdf.format(calFrom.getTime()) );
                calFrom.add(Calendar.DATE, 1);
            }
            return dates;
        }

        protected void initDatabases(){
            localDatabase = InitDatabases.buildLocalDatabase(mContext);
            remoteDatabase = InitDatabases.buildRemoteDatabase(mContext);
        }

    }
}

