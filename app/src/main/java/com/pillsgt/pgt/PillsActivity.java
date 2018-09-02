package com.pillsgt.pgt;

import android.app.DatePickerDialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pillsgt.pgt.databases.LocalDatabase;
import com.pillsgt.pgt.databases.RemoteDatabase;
import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.models.remote.Keyword;
import com.pillsgt.pgt.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PillsActivity extends AppCompatActivity {

    public static LocalDatabase localDatabase;
    public static RemoteDatabase remoteDatabase;

    protected Integer cID = null;

    Calendar startDate =Calendar.getInstance();
    Calendar endDate =Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pills);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PillsActivity.this,MainActivity.class));
                Snackbar.make(view, "Loading...", Snackbar.LENGTH_LONG)
                        .setAction("Back", null).show();
            }
        });

        initDatabases();
        initControls();
    }

    protected void initDatabases(){
        localDatabase = Room.databaseBuilder(getApplicationContext(),LocalDatabase.class, Utils.localDbName)
                .allowMainThreadQueries()
                .build();

        remoteDatabase = Room.databaseBuilder(getApplicationContext(),RemoteDatabase.class, Utils.downloadDbName)
                .allowMainThreadQueries()
                .build();
    }

    //Autocomplete field
    protected void initPillName() {

        //todo:must be from AJAX
        List<String> pillsList = new ArrayList<String>();

        List<Keyword> keywords = remoteDatabase.remoteDAO().getKeywords();
        for (final Keyword keyword : keywords ) {
            pillsList.add(keyword.getKeyword());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice, pillsList);
        AutoCompleteTextView acTextView = findViewById(R.id.pills);
        acTextView.setThreshold(3);
        acTextView.setAdapter(adapter);
    }
    protected void initCronType(){
        Spinner spinner = findViewById(R.id.cron_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cron_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    protected void initCronInterval(){
        Spinner spinner = findViewById(R.id.cron_interval);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cron_interval, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    //start and end dates block
    private void setInitialDate() {
        TextView startDateInput = findViewById(R.id.startDate);
        TextView endDateInput = findViewById(R.id.endDate);

        startDateInput.setText(DateUtils.formatDateTime(this,
                startDate.getTimeInMillis(),
                        DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_MONTH
                        | DateUtils.FORMAT_SHOW_YEAR ));

        endDateInput.setText(DateUtils.formatDateTime(this,
                endDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_MONTH
                        | DateUtils.FORMAT_SHOW_YEAR));
    }

    public void setStartDate(View v) {
        new DatePickerDialog(PillsActivity.this, ds,
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH))
                .show();
    }
    public void setEndDate(View v) {
        new DatePickerDialog(PillsActivity.this, de,
                endDate.get(Calendar.YEAR),
                endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    DatePickerDialog.OnDateSetListener ds=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startDate.set(Calendar.YEAR, year);
            startDate.set(Calendar.MONTH, monthOfYear);
            startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDate();
        }
    };
    DatePickerDialog.OnDateSetListener de=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            endDate.set(Calendar.YEAR, year);
            endDate.set(Calendar.MONTH, monthOfYear);
            endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDate();
        }
    };

    //Spinner with cron types
    //todo: items must be from config
    protected void initControls(){
        initPillName();
        initCronType();
        initCronInterval();
        setInitialDate();

        initEdit();
    }

    protected void initEdit(){
        Intent intent = getIntent();
        String pillRuleId = intent.getStringExtra("pillRuleId");

        Button submitButton = (Button) findViewById(R.id.manage_pills);

        if(pillRuleId == null || pillRuleId.isEmpty()){
            submitButton.setText("Добавить");
            return;
        }
        submitButton.setText("Редактировать");

        cID = Integer.valueOf(pillRuleId);

        //get data for fill form
        PillRule pillRule = localDatabase.localDAO().loadRuleById(cID);

        //fill data by id pull rule
        AutoCompleteTextView pillName = findViewById(R.id.pills);
        pillName.setThreshold(Integer.MAX_VALUE);//hook
        pillName.setText( pillRule.getName() );
        pillName.setThreshold(3);//return 3 chars for starting autocomplete


        Spinner cronTypeInput = findViewById(R.id.cron_type);
        cronTypeInput.setSelection(pillRule.getCron_type());

        Spinner cronIntervalInput = findViewById(R.id.cron_interval);
        cronIntervalInput.setSelection(pillRule.getCron_interval());

        TextView startDateInput = findViewById(R.id.startDate);
        startDateInput.setText( pillRule.getStart_date() );

        TextView endDateInput = findViewById(R.id.endDate);
        endDateInput.setText( pillRule.getEnd_date() );


//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat format = new SimpleDateFormat(Utils.dateTimePatternView );
//        String curDateFormatted = format.format(calendar.getTime());

//        if ( pillRule.getStart_date().length() > 0 ){
//            startDateInput.setText( pillRule.getStart_date() );

//            String startDateFormatted;
//            if ( startDateInput.getText().length() > 0){
//                Date startDateParsed = null;
//                try {
//                    startDateParsed = new SimpleDateFormat(Utils.dateTimePatternView)
//                            .parse( (String) startDateInput.getText() );
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                startDateFormatted = format.format(startDateParsed.getTime());
//            } else {
//                startDateFormatted = curDateFormatted;
//            }
//            startDateInput.setText( startDateFormatted );
//        }

    }


    public void managePills(View view) throws ParseException {
        AutoCompleteTextView pillName = findViewById(R.id.pills);
        Spinner cronTypeInput = findViewById(R.id.cron_type);
        Spinner cronIntervalInput = findViewById(R.id.cron_interval);
        TextView startDateInput = findViewById(R.id.startDate);
        TextView endDateInput = findViewById(R.id.endDate);

        //add to db
        PillRule pillRule = new PillRule();
        Boolean updateRow = false;
        if ( cID != null){
            PillRule sPillRule = localDatabase.localDAO().loadRuleById(cID);
            if (sPillRule != null ){
                pillRule = sPillRule;
                updateRow = true;
            }
        }

        pillRule.setName( pillName.getText().toString() );
        pillRule.setCron_type(cronTypeInput.getSelectedItemPosition());//todo: NOT POSITION, MUST BE ID
        pillRule.setCron_interval(cronIntervalInput.getSelectedItemPosition());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(Utils.dateTimePatternDb );
        String curDateFormatted = format.format(calendar.getTime());
        pillRule.setUpdated_at( curDateFormatted  );

        String startDateFormatted;
        if ( startDateInput.getText().length() > 0){
            Date startDateParsed = new SimpleDateFormat(Utils.dateTimePatternView)
                    .parse( (String) startDateInput.getText() );
            startDateFormatted = format.format(startDateParsed.getTime());
        } else {
            startDateFormatted = curDateFormatted;
        }
        pillRule.setStart_date( startDateFormatted );


        String endDateFormatted = "";
        if ( endDateInput.getText().length() > 0){
            Date endDateParsed = new SimpleDateFormat(Utils.dateTimePatternView)
                    .parse( (String) endDateInput.getText() );
            endDateFormatted = format.format(endDateParsed.getTime());
        }
        pillRule.setEnd_date( endDateFormatted );


        if ( updateRow == true){
            localDatabase.localDAO().updateRule(pillRule);
        } else {
            pillRule.setCreated_at( curDateFormatted );
            localDatabase.localDAO().addRule(pillRule);
        }

        //success saved, redirect to index page with list
        startActivity(new Intent(PillsActivity.this,MainActivity.class));
        Toast.makeText(getApplicationContext(), "Данные сохранены!", Toast.LENGTH_SHORT).show();
    }

}
