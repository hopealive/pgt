package com.pillsgt.pgt;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.Toolbar;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = findViewById(R.id.bottom_nav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //left menu
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();//so interesting hook for menu
        navigationView.setNavigationItemSelectedListener(this);

        renderPillsList();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

    protected void renderPillsList() {
        //get data
        List<PillRule> pillRules = localDatabase.localDAO().getRules();

        View ruleBlock =  findViewById(R.id.index_pill_rules_list);
        ruleBlock.setPadding(10, 0,10,10);

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
            ruleRow.setPadding(0, 0,0,10);

            //block with texts
            LinearLayout textBlockLayout = new LinearLayout(this);
            textBlockLayout.setLayoutParams(new LinearLayout.LayoutParams((int) (screenSize.x * 2 / 3), LayoutParams.MATCH_PARENT, 2f));
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
            ruleDescription.setText( ruleDescriptionText );
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
                    Toast.makeText(getApplicationContext(), R.string.message_success_row_deleted, Toast.LENGTH_SHORT).show();
                    refresh();
                }
            });
            btn_remove.setImageResource(android.R.drawable.ic_delete);
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
            btn_edit.setImageResource(android.R.drawable.ic_input_get);
            ((LinearLayout) buttonLayout).addView(btn_edit);

            ruleRow.addView(buttonLayout);
            ((LinearLayout) ruleBlock).addView(ruleRow);

        }

        TextView mainPillCounter = findViewById(R.id.mainPillCounter);
        mainPillCounter.setText( Integer.toString( pillRules.size() ) );
    }

}
