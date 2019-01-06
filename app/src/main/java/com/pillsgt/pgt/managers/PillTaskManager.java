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

            String pillName = pillsUa.getOriginal_name();
            String pillDescription = pillsUa.getDosage_form();

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

            List<String> pillTaskDates = getDateList(pillRule);
            List<String> pillTaskTimes = getTimeList(pillRule);

            Calendar alertDateCalendar = Calendar.getInstance();
            SimpleDateFormat sdFormat = new SimpleDateFormat(Utils.dateTimePatternDb );

            String[] dateParsed;
            String[] timeParsed;
            for ( String pillTaskDate : pillTaskDates ){
                dateParsed = pillTaskDate.split("-");
                alertDateCalendar.set(Calendar.YEAR, Integer.parseInt(dateParsed[0]));

                int mMonth = Integer.parseInt(dateParsed[1])-1;
                alertDateCalendar.set(Calendar.MONTH, mMonth);
                alertDateCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParsed[2]));

                for ( String pillTaskTime : pillTaskTimes ){
                    timeParsed = pillTaskTime.split(":");
                    alertDateCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParsed[0]));
                    alertDateCalendar.set(Calendar.MINUTE, Integer.parseInt(timeParsed[1]));
                    alertDateCalendar.set(Calendar.SECOND, 0);

                    if (alertDateCalendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() ){
                        continue;
                    }
                    sdFormat.setTimeZone( TimeZone.getTimeZone("GMT") );
                    String alertTime = sdFormat.format(alertDateCalendar.getTime());
                    savePillTask(pillRule, alertTime, pillName, pillDescription);
                }
            }

            localDatabase.close();
            remoteDatabase.close();
            return null;
        }

        //save tasks
        protected void savePillTask(PillRule pillRule, String alertTime, String pillName, String pillDescription){

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdFormat = new SimpleDateFormat(Utils.dateTimePatternDb );
            sdFormat.setTimeZone( TimeZone.getTimeZone("GMT") );
            String curDateFormatted = sdFormat.format(calendar.getTime());

            PillTask pillTask = new PillTask();
            pillTask.setRule_id(pillRule.getId());
            pillTask.setTitle( pillRule.getName() );
            pillTask.setShort_title( pillName );
            pillTask.setDescription( pillDescription );
            pillTask.setStatus( PillTask.STATUS_NEW );


//debug data. +1 minute. //todo: dev mode
//            Calendar nextMinDate = Calendar.getInstance();
//            nextMinDate.add(Calendar.MINUTE, 1);
//            String alertDateFormatted = sdFormat.format(nextMinDate.getTime())
//            pillTask.setAlarm_at(alertDateFormatted);

            pillTask.setAlarm_at(alertTime);
            pillTask.setUpdated_at(curDateFormatted);
            pillTask.setCreated_at(curDateFormatted);
            localDatabase.localDAO().addPillTask(pillTask);
        }

        protected List<String> getTimeList(PillRule pillRule){
            List<String> list = new ArrayList<String>();

            List<PillTimeRule> pillTimeRules = localDatabase.localDAO().loadTimeRulesByRuleId(pillRule.getId());
            for ( PillTimeRule pillTimeRule : pillTimeRules){
                list.add(pillTimeRule.getAlarm_at());
            }

            return list;
        }

        protected List<String> getDateList(PillRule pillRule){
            SimpleDateFormat dbSdf = new SimpleDateFormat(Utils.dateTimePatternDb );
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = pillRule.getStart_date();
            String endDate = pillRule.getEnd_date();

            //if user set option Is_continues
            //endDate = startDate + 1month
            if ( pillRule.getIs_continues() == 1 ){
                try {
                    Date startDateContinue = sdf.parse(startDate);
                    Calendar calStartDateContinue = Calendar.getInstance();
                    calStartDateContinue.setTime(startDateContinue);
                    calStartDateContinue.add(Calendar.MONTH, 1);
                    endDate = dbSdf.format(calStartDateContinue.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            ArrayList<String> dates = new ArrayList<String>();

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

