package com.pillsgt.pgt.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pillsgt.pgt.databases.InitDatabases;
import com.pillsgt.pgt.databases.LocalDatabase;
import com.pillsgt.pgt.databases.RemoteDatabase;
import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.models.PillTask;
import com.pillsgt.pgt.models.remote.PillsUa;
import com.pillsgt.pgt.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
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
                Log.e(TAG, "Error While deleting tasks. Exception " + e.getMessage());
            }


            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdFormat = new SimpleDateFormat(Utils.dateTimePatternDb );
            sdFormat.setTimeZone( TimeZone.getTimeZone("GMT") );
            String curDateFormatted = sdFormat.format(calendar.getTime());

            //save tasks
            PillTask pillTask = new PillTask();
            pillTask.setRule_id(ruleId);
            pillTask.setTitle( pillRule.getName() );
            pillTask.setShort_title( pillsUa.getOriginal_name() );
            pillTask.setDescription( pillsUa.getDosage_form() );
            pillTask.setStatus( PillTask.STATUS_NEW );

//pillRule.getCron_interval()
//pillRule.getCron_type()

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
            List<String>tList = Arrays.asList();
            return tList;
        }

        protected List<String> getDateList(PillRule pillRule){
//pillRule.getFrequency_type()
//pillRule.getFrequency_value() //todo: make for this
//pillRule.getIs_continues()
//            pillRule.getStart_date()
//            pillRule.getEnd_date()

            List<String>dList = Arrays.asList();
            return dList;
        }

        protected void initDatabases(){
            localDatabase = InitDatabases.buildLocalDatabase(mContext);
            remoteDatabase = InitDatabases.buildRemoteDatabase(mContext);
        }


    }

}

