package com.pillsgt.pgt;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import com.pillsgt.pgt.databases.InitDatabases;
import com.pillsgt.pgt.databases.LocalDatabase;
import com.pillsgt.pgt.models.PillTask;
import com.pillsgt.pgt.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class RemindService  extends IntentService {

    final String TAG = "REMIND";

    private static LocalDatabase localDatabase;


    private boolean notificationHasVibration;
    private String notificationRingtone;
    public static final int DEFAULT_NOTIFICATION_ID = 10;

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
        localDatabase = InitDatabases.buildLocalDatabase(getApplicationContext());
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

        //todo: get and make system notifications
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean prefNotificationsNewMessage = prefs.getBoolean("notifications_new_message", true);
        if(!prefNotificationsNewMessage) return;

        notificationHasVibration = prefs.getBoolean("notifications_new_message_vibrate", true);
        notificationRingtone = prefs.getString("notifications_new_message_ringtone", "");


        checkDate.add(Calendar.MINUTE, 1);
        String futureDateF = sdFormat.format(checkDate.getTime());

        checkDate.add(Calendar.MINUTE, -30);
        String missedDateF = sdFormat.format(checkDate.getTime());

        //Get pill tasks to notify
        PillTask retryPillTask = localDatabase.localDAO().loadNotifyTask(missedDateF, futureDateF, PillTask.STATUS_NOTTIFIED);
        if (retryPillTask == null ){
            //First notify
            PillTask pillTask = localDatabase.localDAO().loadNotifyTask(missedDateF, futureDateF, PillTask.STATUS_NEW);
            if (pillTask != null ){
                pillTaskProcess(pillTask);
            }
        } else {
            //Retry notify
            pillTaskProcess(retryPillTask);
        }

    }

    protected void pillTaskProcess( PillTask pillTask ) {
        //activity
        Intent dialogIntent = new Intent(this, AlertActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra("alertPillTaskId", pillTask.getId());
        startActivity(dialogIntent);

        //send notify
        String Ticker = getResources().getString(R.string.app_shrot_name)+". "+pillTask.getShort_title();
        sendPillNotification(Ticker, pillTask);

        //update status
        pillTask.setStatus(PillTask.STATUS_NOTTIFIED);
        localDatabase.localDAO().updatePillTask(pillTask);
        localDatabase.close();
    }

    //Send custom notification
    public void sendPillNotification(String Ticker, PillTask pillTask) {
        String Title = pillTask.getShort_title();
        String Description = pillTask.getDescription();

        //These three lines makes Notification to open main activity after clicking on it
        Intent notificationIntent = new Intent(this, AlertActivity.class);
        notificationIntent.putExtra("alertPillTaskId", pillTask.getId());
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(contentIntent)
                .setOngoing(false) //Can be swiped out
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setTicker(Ticker)
                .setContentTitle(Title)
                .setContentText(Description);

        if ( notificationRingtone != null ){
            builder.setSound(Uri.parse(notificationRingtone));
        }

        // Start without a delay
        // Vibrate for 100 milliseconds
        // Sleep for 2000 milliseconds
        long[] VibrateTime = {0, 100, 300, 200, 300, 100, 300, 200, 200};
        if (notificationHasVibration){
            builder.setVibrate(VibrateTime);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "my_channel_"+pillTask.getId();

            NotificationChannel mChannel = new NotificationChannel(channelId,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);

            if (notificationHasVibration){
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(VibrateTime);
            }

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            Uri sound = Uri.parse(notificationRingtone);
            mChannel.setSound(sound, attributes); // This is IMPORTANT

            notificationManager.createNotificationChannel(mChannel);
            builder.setChannelId(channelId);
        }
        notificationManager.notify(DEFAULT_NOTIFICATION_ID*pillTask.getId(), builder.build());
    }

}
