package com.pillsgt.pgt;

import android.graphics.Point;
import android.os.Bundle;
import android.arch.persistence.room.Room;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.utils.Utils;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static MyAppDatabase myAppDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMyDatabase();
        renderPillsList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,PillsActivity.class));
                Snackbar.make(view, "Loading...", Snackbar.LENGTH_LONG)
                        .setAction("addPills", null).show();
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        refresh();
    }

    protected void initMyDatabase() {
        myAppDatabase = Room.databaseBuilder(getApplicationContext(),MyAppDatabase.class, Utils.localDbName)
                .allowMainThreadQueries()
                .build();
    }

    protected void renderPillsList() {
        //get data
        List<PillRule> pillRules = myAppDatabase.myDAO().getRules();

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
                    myAppDatabase.myDAO().deleteRuleById( btn_remove.getId() );
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
