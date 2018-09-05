package com.pillsgt.pgt;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.pillsgt.pgt.AlertActivity;
import com.pillsgt.pgt.MainActivity;
import com.pillsgt.pgt.R;

public class RemindService  extends IntentService {

    final String TAG = "REMIND";
//    private NotificationManager notificationManager;
    public static final int DEFAULT_NOTIFICATION_ID = 11;

    public RemindService(){
        super("RemindService");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        process();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //custom methods don't remove
    public void process() {
        Log.d(TAG, "process");//todo: remove

        //todo:
        //get task to notify


        //good, need to send ID
        Intent dialogIntent = new Intent(this, AlertActivity.class);
//        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        dialogIntent.putExtra("ruleId", 111);//todo: get id from DB
//        startActivity(dialogIntent);

//        String Title = "АЗИТРОМИЦЫН";
//        String Description = "Надо выпить после еды АЗИТРОМИЦЫН";
//        sendNotification(Title, Description);//TODO: good method make more by cron

//        sendNotification("TEST Ticker","TEST MainActivity");//TODO: good method make more by cron
    }

    //Send custom notification
    public void sendNotification(String Ticker, String Text) {
        //These three lines makes Notification to open main activity after clicking on it
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String Title = getResources().getString(R.string.app_shrot_name);

        // Start without a delay
        // Vibrate for 100 milliseconds
        // Sleep for 1000 milliseconds
        long[] VibrateTime = {0, 100, 1000};

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(contentIntent)
                .setOngoing(false)   //Can be swiped out
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(Ticker)
                .setVibrate(VibrateTime)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle(Title)
                .setContentText(Text);

        Notification notification = builder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(DEFAULT_NOTIFICATION_ID, notification);
    }

}
