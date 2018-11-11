package com.pillsgt.pgt;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pillsgt.pgt.models.remote.PillsUa;
import com.pillsgt.pgt.utils.Utils;

import java.util.List;

public class MedicalsActivity extends AppActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MaTAG";


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return Utils.bottomOnNavigationItemSelected(item, MedicalsActivity.this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicals);
        initMainMenu(mOnNavigationItemSelectedListener, this);

        //set default bottom item
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.bottom_nav_medicals);
        menuItem.setChecked(true);

        renderMedicalsList();
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
        Utils.rightOnNavigationItemSelected(item, MedicalsActivity.this);
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Utils.leftOnNavigationItemSelected(item, MedicalsActivity.this);
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

    protected void renderMedicalsList() {
        //get data
        List<Integer> pillIds = localDatabase.localDAO().getPillIds();
        List<PillsUa> pillsUa = remoteDatabase.remoteDAO().loadPillsUaByIntIds(pillIds);

        //View
        View listBlock =  findViewById(R.id.main_body_list);
        listBlock.setPadding(10, 0,5,10);

        for (final PillsUa pillUa : pillsUa ){

            LinearLayout listRow = new LinearLayout(this);
            listRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            listRow.setOrientation(LinearLayout.HORIZONTAL);
            listRow.setPadding(0, 5,0,5);

            //block with texts
            LinearLayout textBlockLayout = new LinearLayout(this);
            textBlockLayout.setOrientation(LinearLayout.VERTICAL);

            TextView itemTitle = new TextView(this);
            itemTitle.setText( pillUa.getOriginal_name() );
            itemTitle.setTypeface(null, Typeface.BOLD);
            textBlockLayout.addView(itemTitle);

            TextView itemDescription = new TextView(this);
            String itemDescriptionText = pillUa.getDosage_form();
            if ( pillUa.getOriginal_name().length() > 100 ){
                itemDescriptionText = itemDescriptionText.substring(0, Math.min(itemDescriptionText.length(), 100));
                itemDescriptionText += "...";
            }
            itemDescriptionText += "\n";
            itemDescription.setText( itemDescriptionText );
            textBlockLayout.addView(itemDescription);

            listRow.addView(textBlockLayout);
            ((LinearLayout) listBlock).addView(listRow);

        }

    }

}
