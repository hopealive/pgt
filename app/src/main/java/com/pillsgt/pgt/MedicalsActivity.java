package com.pillsgt.pgt;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
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

        setMedicalCounter();
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

        Point screenSize = Utils.getScreenSize(getWindowManager().getDefaultDisplay() );

        for (final PillsUa pillUa : pillsUa ){

            LinearLayout listRow = new LinearLayout(this);
            listRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            listRow.setOrientation(LinearLayout.HORIZONTAL);
            listRow.setPadding(0, 5,5,5);

            //block with texts
            LinearLayout textBlockLayout = new LinearLayout(this);
            textBlockLayout.setLayoutParams(new LinearLayout.LayoutParams( (screenSize.x * 4/5), LinearLayout.LayoutParams.MATCH_PARENT, 2f));
            textBlockLayout.setOrientation(LinearLayout.VERTICAL);
            textBlockLayout.setPadding(0, 0,20,0);

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


            //block with buttons
            View buttonLayout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            buttonLayout.setLayoutParams(params);

            final FloatingActionButton btn_show = new FloatingActionButton(this);
            btn_show.setId(pillUa.getId());
            btn_show.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MedicalsActivity.this,MedicalActivity.class);
                    i.putExtra("pillId", Integer.valueOf(btn_show.getId()) );
                    startActivity(i);
                }
            });
            btn_show.setImageResource(R.drawable.ic_baseline_pageview_24px);
            btn_show.setSize(android.support.design.widget.FloatingActionButton.SIZE_MINI);
            ((LinearLayout) buttonLayout).addView(btn_show);


            listRow.addView(buttonLayout);

            ((LinearLayout) listBlock).addView(listRow);

        }

    }

}
