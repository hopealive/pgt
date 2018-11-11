package com.pillsgt.pgt;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.models.remote.PillsUa;
import com.pillsgt.pgt.utils.Utils;

import java.util.List;

public class MedicalActivity extends AppActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MlTAG";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return Utils.bottomOnNavigationItemSelected(item, MedicalActivity.this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical);
        initMainMenu(mOnNavigationItemSelectedListener, this);

        //set default bottom item
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.bottom_nav_medicals);
        menuItem.setChecked(true);

        setMedicalCounter();
        renderMedical();
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
        Utils.rightOnNavigationItemSelected(item, MedicalActivity.this);
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Utils.leftOnNavigationItemSelected(item, MedicalActivity.this);
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

    protected void renderMedical() {
        //get data
        Intent intent = getIntent();
        Integer pillId = intent.getIntExtra("pillId", 0);

        if ( pillId > 0 ){
            //get pill data
            PillsUa pillUa = remoteDatabase.remoteDAO().loadPillsUa(pillId);
            TextView medicalTitle = findViewById(R.id.medicalTitle);
            medicalTitle.setText(pillUa.getOriginal_name());
            TextView medicalDescription = findViewById(R.id.medicalDescription);
            medicalDescription.setText(pillUa.getDosage_form());

            //get rules
            List<PillRule> pillRules = localDatabase.localDAO().getRulesByPillId(pillId);
        }
    }

}
