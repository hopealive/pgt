package com.pillsgt.pgt;

import android.graphics.Point;
import android.os.Bundle;
import android.arch.persistence.room.Room;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.pillsgt.pgt.databases.LocalDatabase;
import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.utils.Utils;

import java.util.List;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static LocalDatabase localDatabase;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                    return true;
                case R.id.navigation_add:
                    startActivity(new Intent(MainActivity.this,PillsActivity.class));
                    return true;
                case R.id.navigation_medicals:
                    //todo: make another activity class
Log.i("BOTTOM", "open medicals");//todo: remove
//                    startActivity(new Intent(MainActivity.this,NavActivity.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//bottom menu
//        BottomNavigationView navigation = findViewById(R.id.navigation);//todo: uncomment and fix
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);//todo: uncomment and fix

        FloatingActionButton fabAdd = findViewById(R.id.floatingAddButton);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,PillsActivity.class));
            }
        });


        //left menu
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();//so interesting hook for menu
        navigationView.setNavigationItemSelectedListener(this);


        initMyDatabase();
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
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    /**
     * Right menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.r_menu_settings) {
            return true;
        } else if (id == R.id.r_menu_logout){
            return true;
        } else {

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        item.setChecked(true);

        if (id == R.id.l_nav_dashboard) {
            startActivity(new Intent(MainActivity.this,MainActivity.class));
        } else if (id == R.id.l_nav_add) {
            startActivity(new Intent(MainActivity.this,PillsActivity.class));
        } else if (id == R.id.l_nav_medicals) {
Log.d("lMENU", "MEDICALS");//todo: remove
        } else if (id == R.id.l_nav_profile) {
Log.d("lMENU", "PROFILE");//todo: remove
        } else if (id == R.id.l_nav_settings) {
Log.d("lMENU", "SETTINGS");//todo: remove
        } else if (id == R.id.l_nav_terms) {
            startActivity(new Intent(MainActivity.this,TermsActivity.class));
        } else if (id == R.id.l_nav_rules) {
            startActivity(new Intent(MainActivity.this,TermsActivity.class));//todo: create rules activity
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onRestart() {
        super.onRestart();
        refresh();
    }

    protected void initMyDatabase() {
        localDatabase = Room.databaseBuilder(getApplicationContext(), LocalDatabase.class, Utils.localDbName)
                .allowMainThreadQueries()
                .build();
    }

    protected void renderPillsList() {
        //get data
        List<PillRule> pillRules = localDatabase.localDAO().getRules();

        View ruleBlock =  findViewById(R.id.index_pill_rules_list);
        ruleBlock.setPadding(10, 0,10,10);


        String[] cron_type_list = getResources().getStringArray(R.array.cron_type);
        String[] cron_interval_list = getResources().getStringArray(R.array.cron_interval);

        Point screenSize = Utils.getScreenSize(getWindowManager().getDefaultDisplay() );

        int i = 0;
        String ruleDescriptionText;
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
            if ( rule.getCron_type() == 3 || rule.getCron_type() == 4 ){ //todo: not position, must be ID
                ruleDescriptionText += getResources().getString(R.string.label_when) + ": "
                        + cron_type_list[rule.getCron_type()] + "\n";
            } else {
                ruleDescriptionText += cron_type_list[rule.getCron_type()] + " ";
            }

            if ( rule.getCron_interval() == 11){ //todo: not position, must be ID
                ruleDescriptionText += getResources().getString(R.string.label_how) + ": "
                        + cron_interval_list[rule.getCron_interval()] + ". ";
            } else {
                ruleDescriptionText += cron_interval_list[rule.getCron_interval()] + ". ";
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

            ++i;
        }

        TextView mainPillCounter = findViewById(R.id.mainPillCounter);
        mainPillCounter.setText( Integer.toString( pillRules.size() ) );
    }


    private void refresh() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

}
