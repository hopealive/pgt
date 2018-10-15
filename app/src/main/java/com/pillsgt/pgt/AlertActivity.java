package com.pillsgt.pgt;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pillsgt.pgt.models.PillTask;
import com.pillsgt.pgt.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class AlertActivity extends AppActivity {

    private static final String TAG = "AlertState";
    private PillTask pillTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pillTask = getPillTask();
        if ( pillTask == null ){
            startActivity(new Intent(AlertActivity.this,MainActivity.class));
            Toast.makeText(getApplicationContext(), R.string.message_data_saved, Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.activity_alert);

        initAlert();
    }

    protected void initAlert() {
        TextView alertTitle = findViewById(R.id.alertTitle);
        alertTitle.setText("");

        TextView alertDescription = findViewById(R.id.alertDescription);
        alertDescription.setText("");

        if ( pillTask != null ){
            if ( pillTask.getTitle().length() > 0){
                alertTitle.setText(pillTask.getTitle());
            }

            if ( pillTask.getDescription().length() > 0 ){
                alertDescription.setText(pillTask.getDescription());
            }
        }
    }

    protected PillTask getPillTask() {
        pillTask = new PillTask();

        Intent intent = getIntent();
        Integer pillTaskId = intent.getIntExtra("alertPillTaskId", 0);
        if ( pillTaskId > 0 ){
            pillTask = localDatabase.localDAO().loadPillTaskById(pillTaskId);
        }
        return pillTask;
    }

    public void saveAlert(View fabView) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        PillTask pillTask = getPillTask();
        if ( pillTask == null){
            return;
        }

        switch (fabView.getId()){
            case R.id.takeAlertButton:
                pillTask.setStatus(PillTask.STATUS_TAKED);
                break;
            case R.id.laterAlertButton:
                pillTask.setStatus(PillTask.STATUS_NOTTIFIED);

                Calendar curDate = Calendar.getInstance();
                SimpleDateFormat sdFormat = new SimpleDateFormat(Utils.dateTimePatternDb );
                sdFormat.setTimeZone( TimeZone.getTimeZone("GMT") );
                curDate.add(Calendar.MINUTE, 10);
                String laterAlarmAt = sdFormat.format(curDate.getTime());

                pillTask.setAlarm_at( laterAlarmAt );
                break;
            case R.id.cancelAlertButton:
                pillTask.setStatus(PillTask.STATUS_CANCELED);
                break;
        }
        localDatabase.localDAO().updatePillTask(pillTask);
        localDatabase.close();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(RemindService.DEFAULT_NOTIFICATION_ID*pillTask.getId());
    }


}
