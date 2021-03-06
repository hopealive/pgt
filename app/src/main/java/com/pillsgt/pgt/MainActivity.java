package com.pillsgt.pgt;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.pillsgt.pgt.managers.CronManager;
import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.utils.Utils;

import java.util.List;


public class MainActivity extends AppActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return Utils.bottomOnNavigationItemSelected(item, MainActivity.this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMainMenu(mOnNavigationItemSelectedListener, this);

        setTodayTasksCounter();
        renderPillsList();
    }

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onResume() {
        super.onResume();
        this.doubleBackToExitPressedOnce = false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else  {
            if ( doubleBackToExitPressedOnce ){
                finishAffinity();
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.exit_press_back_twice_message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Right menu
     * Inflate the menu; this adds items to the action bar if it is present.
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.right_nav, menu);
        return true;
    }

    /**
     * Right menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Utils.rightOnNavigationItemSelected(item, MainActivity.this);
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Utils.leftOnNavigationItemSelected(item, MainActivity.this);
        return true;
    }


    @Override
    public void onRestart() {
        super.onRestart();
        refresh();
    }

    private void refresh() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    protected void setTodayTasksCounter() {
        TextView mainCounter = findViewById(R.id.mainCounter);
        mainCounter.setText( localDatabase.localDAO().countPillTasks() );
    }

    protected void renderPillsList() {
        //get data
        List<PillRule> pillRules = localDatabase.localDAO().getRules();

        View ruleBlock =  findViewById(R.id.main_body_list);
        ruleBlock.setPadding(10, 0,5,10);

        CronManager cronManager = new CronManager();
        String[] cron_type_list = getResources().getStringArray(R.array.cron_type);
        String[] cron_interval_list = getResources().getStringArray(R.array.cron_interval);

        Point screenSize = Utils.getScreenSize(getWindowManager().getDefaultDisplay() );


        String ruleDescriptionText;
        int cronTypePosition;
        int cronIntervalPosition;
        for (final PillRule rule : pillRules ){
            LinearLayout ruleRow = new LinearLayout(this);
            ruleRow.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            ruleRow.setOrientation(LinearLayout.HORIZONTAL);
            ruleRow.setPadding(0, 5,0,5);

            //block with texts
            LinearLayout textBlockLayout = new LinearLayout(this);
            textBlockLayout.setLayoutParams(new LinearLayout.LayoutParams( (screenSize.x * 3/4), LayoutParams.MATCH_PARENT, 2f));
            textBlockLayout.setOrientation(LinearLayout.VERTICAL);

            TextView ruleTitle = new TextView(this);
            ruleTitle.setText( rule.getName() );
            ruleTitle.setTypeface(null, Typeface.BOLD);
            textBlockLayout.addView(ruleTitle);

            TextView ruleDescription = new TextView(this);
            ruleDescriptionText = "";

            cronTypePosition = cronManager.getTypePosition( rule.getCron_type() );
            if ( rule.getCron_type() == 100 ){
                ruleDescriptionText += getResources().getString(R.string.label_when) + ": "
                    + cron_type_list[cronTypePosition] + "\n";
            } else if (cronTypePosition >= 0) {
                ruleDescriptionText += cron_type_list[cronTypePosition] + " ";
            }

            cronIntervalPosition = cronManager.getIntervalPosition( rule.getCron_interval() );
            if ( rule.getCron_interval() == 100){
                ruleDescriptionText += getResources().getString(R.string.label_how) + ": "
                    + cron_interval_list[cronIntervalPosition] + ". ";
            } else if ( cronIntervalPosition >= 0 ) {
                ruleDescriptionText += cron_interval_list[cronIntervalPosition] + ". ";
            }
            ruleDescription.setText( ruleDescriptionText + "\n" );
            textBlockLayout.addView(ruleDescription);

            ruleRow.addView(textBlockLayout);


            //block with buttons
            View buttonLayout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f);
            buttonLayout.setLayoutParams(params);

            final FloatingActionButton btn_remove = new FloatingActionButton(this);
            btn_remove.setId(rule.getId());
            btn_remove.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    localDatabase.localDAO().deleteRuleById( btn_remove.getId() );
                    localDatabase.localDAO().deletePillTaskByRuleId( btn_remove.getId() );
                    Toast.makeText(getApplicationContext(), R.string.message_success_row_deleted, Toast.LENGTH_SHORT).show();
                    refresh();
                }
            });
            btn_remove.setImageResource(R.drawable.ic_baseline_clear_24px);
            btn_remove.setSize(android.support.design.widget.FloatingActionButton.SIZE_MINI);
            ((LinearLayout) buttonLayout).addView(btn_remove);

            final FloatingActionButton btn_edit = new FloatingActionButton(this);
            btn_edit.setId(rule.getId());
            btn_edit.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this,PillsActivity.class);
                    i.putExtra("pillRuleId", String.valueOf(btn_edit.getId()) );
                    startActivity(i);
                }
            });
            btn_edit.setImageResource(R.drawable.ic_baseline_create_24px);
            btn_edit.setSize(android.support.design.widget.FloatingActionButton.SIZE_MINI);
            ((LinearLayout) buttonLayout).addView(btn_edit);


            ruleRow.addView(buttonLayout);
            ((LinearLayout) ruleBlock).addView(ruleRow);
        }
    }

}
