package com.pillsgt.pgt.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;

import com.pillsgt.pgt.ConfidenceActivity;
import com.pillsgt.pgt.MainActivity;
import com.pillsgt.pgt.PillsActivity;
import com.pillsgt.pgt.R;
import com.pillsgt.pgt.TermsActivity;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class Utils {

    private final String TAG = "UTILS";

    //String Values to be Used in App
    public static final int alaramManagerPeriod = 60;
    public static final String mainUrl = "http://pillsgt.com/api/index";
    public static final String downloadFileArchiveName = "pillsgt-list.db.zip";
    public static final String remoteDbName = "pillsgt-list.db";
    public static final String localDbName = "pilldb";

    public static final String dateTimePatternDb = "yyyy-MM-dd HH:mm:ss z";
    public static final String dateTimePatternView = "MMM dd, yyyy";

    public static final List<String> defaultUserSettingFields = Arrays.asList(
            "display_name",
            "db_version",
            "default_language",
            "sync_frequency",
            "last_update_date",
            "notifications_enbale",
            "notifications_ringtone",
            "notifications_vibrate",
            "task_start_date",
            "eat_duration"
    );

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


    /**
     * Before some download from web check for SD Card storage exists
     * @return boolean
     */
    public static boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }


    /**
     * Check if internet is present or not
     * @return boolean
     */
    public static boolean isConnectingToInternet( ConnectivityManager connectivityManager ) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    /**
     *
     * @param display
     * @return
     */
    public static Point getScreenSize(Display display) {
        Point size = new Point();
        try {
            display.getRealSize(size);
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }
        return size;
    }



    /**
     * ###############
     * MENU BLOCK
     * ###############
     */

    /**
     * Left menu
     * @param item
     * @param activity
     */
    public static void leftOnNavigationItemSelected(MenuItem item, Activity activity){
        int id = item.getItemId();
        item.setChecked(true);

        if (id == R.id.left_nav_dashboard) {
            activity.startActivity(new Intent(activity, MainActivity.class));
        } else if (id == R.id.left_nav_add) {
            activity.startActivity(new Intent(activity,PillsActivity.class));
        } else if (id == R.id.left_nav_medicals) {
            Log.d("LMENU", "MEDICALS");//todo: remove
        } else if (id == R.id.left_nav_profile) {
            Log.d("LMENU", "PROFILE");//todo: remove
        } else if (id == R.id.left_nav_settings) {
            Log.d("LMENU", "SETTINGS");//todo: remove
        } else if (id == R.id.left_nav_terms) {
            activity.startActivity(new Intent(activity,TermsActivity.class));
        } else if (id == R.id.left_nav_confidence) {
            activity.startActivity(new Intent(activity,ConfidenceActivity.class));
        }

        DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    /**
     * Right menu
     * @param item
     * @param activity
     */
    public static void rightOnNavigationItemSelected(MenuItem item, Activity activity){
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.right_nav_settings) {
            Log.d("RIGHT_MENU", "right_nav_settings");//TODO: make activiity
//        } else if (id == R.id.right_nav_logout){
//            Log.d("RIGHT_MENU", "right_nav_logout");//TODO: make activiity
        } else {
            Log.d("RIGHT_MENU", "NULL");//TODO: make activiity
        }

    }

    /**
     * Bottom menu
     * @param item
     * @param activity
     * @return boolean
     */
    public static boolean bottomOnNavigationItemSelected(MenuItem item, Activity activity){
            switch (item.getItemId()) {
                case R.id.bottom_nav_dashboard:
                    activity.startActivity(new Intent(activity,MainActivity.class));
                    return true;
                case R.id.bottom_nav_add:
                    activity.startActivity(new Intent(activity,PillsActivity.class));
                    return true;
                case R.id.bottom_nav_medicals:
                    //todo: make another activity class
Log.i("BOTTOM_MENU", "open medicals");//todo: remove
                    return true;
            }
            return false;

    }

}
