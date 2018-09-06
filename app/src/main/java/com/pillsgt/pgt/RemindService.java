package com.pillsgt.pgt;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.util.Log;

import com.pillsgt.pgt.databases.LocalDatabase;
import com.pillsgt.pgt.databases.RemoteDatabase;
import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.models.PillTask;
import com.pillsgt.pgt.models.remote.Keyword;
import com.pillsgt.pgt.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class RemindService  extends IntentService {

    final String TAG = "REMIND";

    private static LocalDatabase localDatabase;
    private static RemoteDatabase remoteDatabase;


    public static final int DEFAULT_NOTIFICATION_ID = 11;

    public RemindService(){
        super("RemindService");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        initDatabases();
        process();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void initDatabases(){
        localDatabase = Room.databaseBuilder(getApplicationContext(),LocalDatabase.class, Utils.localDbName)
                .allowMainThreadQueries()
                .build();

        remoteDatabase = Room.databaseBuilder(getApplicationContext(),RemoteDatabase.class, Utils.remoteDbName)
                .allowMainThreadQueries()
                .build();
    }
    //custom methods don't remove
    public void process() {
        voidTasks();
    }

    public void voidTasks() {
        //get time for compare
        Calendar checkDate = Calendar.getInstance();
        SimpleDateFormat sdFormat = new SimpleDateFormat(Utils.dateTimePatternDb );
        sdFormat.setTimeZone( TimeZone.getTimeZone("GMT") );

        checkDate.add(Calendar.MINUTE, 1);
        String futureDateF = sdFormat.format(checkDate.getTime());

        checkDate.add(Calendar.MINUTE, -10);
        String missedDateF = sdFormat.format(checkDate.getTime());

        //get task to notify
        List<PillTask> pillTasks = localDatabase.localDAO().loadNotifyTasks(missedDateF, futureDateF, PillTask.STATUS_NEW);
        for (final PillTask pillTask : pillTasks ) {
            //activity
            Intent dialogIntent = new Intent(this, AlertActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            dialogIntent.putExtra("ruleId", pillTask.getId());
            startActivity(dialogIntent);

            //send notify
            String Ticker = getResources().getString(R.string.app_shrot_name)+". "+pillTask.getShort_title();
            sendNotification(Ticker, pillTask.getShort_title(), pillTask.getDescription());

            //todo: think about notify status
            //update status
            pillTask.setStatus(PillTask.STATUS_NOTTIFIED);
            localDatabase.localDAO().updatePillTask(pillTask);
        }
    }

    //Send custom notification
    public void sendNotification(String Ticker, String Title, String Description) {
        //These three lines makes Notification to open main activity after clicking on it
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Start without a delay
        // Vibrate for 100 milliseconds
        // Sleep for 2000 milliseconds
        long[] VibrateTime = {0, 100, 2000};

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(contentIntent)
                .setOngoing(false)   //Can be swiped out
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(Ticker)
                .setVibrate(VibrateTime)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle(Title)
                .setContentText(Description);

        Notification notification = builder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(DEFAULT_NOTIFICATION_ID, notification);
    }

}
