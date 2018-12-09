package com.pillsgt.pgt;

import android.app.DatePickerDialog;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pillsgt.pgt.fragments.NumOfDaysFragment;
import com.pillsgt.pgt.fragments.PillsTimeInputFragment;
import com.pillsgt.pgt.fragments.TimePickerFragment;
import com.pillsgt.pgt.managers.keywordautocomplete.PillsAutoCompleteTextChangedListener;
import com.pillsgt.pgt.managers.keywordautocomplete.PillsAutoCompleteView;
import com.pillsgt.pgt.managers.CronManager;
import com.pillsgt.pgt.managers.PillTaskManager;
import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.models.remote.PillsUa;
import com.pillsgt.pgt.utils.Converters;
import com.pillsgt.pgt.utils.PillsDateTimeLists;
import com.pillsgt.pgt.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Thread.sleep;

public class PillsActivity extends AppActivity implements
        NumOfDaysFragment.NodInterface {

    protected Integer cID = null;

    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();
    RadioGroup durationGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pills);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PillsActivity.this,MainActivity.class));
                Snackbar.make(view, "Loading...", Snackbar.LENGTH_LONG)
                        .setAction("Back", null).show();
            }
        });

        initControls();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PillsActivity.this,MainActivity.class));
    }


    public static PillsAutoCompleteView pillsAutoComplete;
    public TextView pillsDescription;
    public ArrayAdapter<String> myAdapter;

    public String[] autoCompleteListItems = new String[] {"..."};

    /**
     * Create input for pill name
     */
    protected void initPillName() {
        pillsAutoComplete = findViewById(R.id.pills);
        pillsAutoComplete.addTextChangedListener(new PillsAutoCompleteTextChangedListener(this));

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, autoCompleteListItems);
        pillsAutoComplete.setThreshold(3);
        pillsAutoComplete.setAdapter(myAdapter);

        pillsDescription = findViewById(R.id.pillsDescription);
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


    /**
     * Set fragment
     */
    protected void initTimeInputs() {
        PillRule pillRule = new PillRule();
        pillRule.setCron_interval(1);
        pillRule.setCron_type(1);
        List<String> timeList = PillsDateTimeLists.getTimeList(pillRule);
        generateTimeInputs(timeList);
    }

    protected void clearTimeInputs(){
        FragmentManager fm = getSupportFragmentManager();
        try {
            if(fm.getFragments()!=null){
                for (int i = 0; i < fm.getBackStackEntryCount(); i++)
                    fm.popBackStack();

                fm.beginTransaction().remove(getSupportFragmentManager()
                        .findFragmentById(R.id.time_input_fragment))
                        .commit();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * List is array, which ite, is time string like "12:00"
     * Insert fragmetns with timepickers
     * @param list
     */
    protected void generateTimeInputs(List<String> list) {
        FragmentManager fm = getSupportFragmentManager();
        int i =1;
        for (String item : list){
            ++i;
            fm.beginTransaction()
                    .add(R.id.time_input_fragment, PillsTimeInputFragment.newInstance(i, item), "fragmentTimeInputs")
                    .commit();
            fm.executePendingTransactions();
        }
    }

    /**
     * BLOCK FOR WORKING WITH DATE START AND FINISH
     */

    /**
     * Set default start date (today) to dialog
     * @param v
     */
    public void setStartDate(View v) {
        new DatePickerDialog(PillsActivity.this, ds,
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    /**
     * Set default end date (today) to dialog
     * @param v
     */
    public void setEndDate(View v) {
        new DatePickerDialog(PillsActivity.this, de,
                endDate.get(Calendar.YEAR),
                endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    //start and end dates block
    private void setInitialDate() {
        SimpleDateFormat format = new SimpleDateFormat(Utils.dateTimePatternView );

        TextView startDateInput = findViewById(R.id.startDate);
        String startDateFormatted = format.format(startDate.getTime());
        startDateInput.setText(startDateFormatted);

        TextView endDateInput = findViewById(R.id.endDate);
        String endDateFormatted = format.format(endDate.getTime());
        endDateInput.setText(endDateFormatted);
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

    private void setInitialSchedule(){
        RadioButton dCheckedPeriod = findViewById(R.id.dCheckedPeriod);
        dCheckedPeriod.setChecked(true);

        durationGroup = findViewById(R.id.duration_group);
        durationGroup.setOnCheckedChangeListener(initDurationGroupListener);

        RadioButton frequency = findViewById(R.id.fEveryDay);
        frequency.setChecked(true);
    }

    private RadioGroup.OnCheckedChangeListener initDurationGroupListener = new RadioGroup.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked) {
                    String checkedRadioButtonName = checkedRadioButton.getResources().getResourceEntryName(checkedRadioButton.getId());
                    switch ( checkedRadioButtonName ){
                        case "dContinuous":
                            break;
                        case "dNumberOfDays":
                            DialogFragment dialogNOD = new NumOfDaysFragment();
                            dialogNOD.show(getSupportFragmentManager(), "NumOfDaysFragment");
                            break;
                    }
                }
        }
    };

    @Override
    public void onNumOfDaysChoose(int numDays) {
        if ( numDays > 0){
            TextView startDateInput = findViewById(R.id.startDate);
            TextView endDateInput = findViewById(R.id.endDate);

            SimpleDateFormat format = new SimpleDateFormat(Utils.dateTimePatternView );

            if ( startDateInput.getText().length() > 0){
                Date startDateParsed;
                try {
                    startDateParsed = new SimpleDateFormat(Utils.dateTimePatternView)
                            .parse((String) startDateInput.getText());
                    Calendar cEndDate = Calendar.getInstance();
                    cEndDate.setTime(startDateParsed);
                    cEndDate.add(Calendar.DAY_OF_MONTH, numDays);

                    String endDateFormatted = format.format(cEndDate.getTime());
                    endDateInput.setText(endDateFormatted);
                } catch ( ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            RadioButton dCheckedPeriod = findViewById(R.id.dCheckedPeriod);
            dCheckedPeriod.setChecked(true);
        }
    }

    protected void initControls(){
        initPillName();
        initCronType();
        initCronInterval();
        initTimeInputs();

        setInitialDate();
        setInitialSchedule();

        initEdit();
    }

    protected void initEdit(){
        Intent intent = getIntent();
        String pillRuleId = intent.getStringExtra("pillRuleId");

        Button submitButton = findViewById(R.id.manage_pills);

        if(pillRuleId == null || pillRuleId.isEmpty()){
            submitButton.setText( R.string.button_add );
            return;
        }
        submitButton.setText( R.string.button_edit );

        cID = Integer.valueOf(pillRuleId);

        //get data for fill form
        PillRule pillRule = localDatabase.localDAO().loadRuleById(cID);

        //fill data by id pull rule
        AutoCompleteTextView pillName = findViewById(R.id.pills);
        pillName.setThreshold(Integer.MAX_VALUE);//hook

        String pillNameValue = pillRule.getName();

        if ( pillRule.getPill_id() > 0 ){
            PillsUa pillUa = remoteDatabase.remoteDAO().loadPillsUa(pillRule.getPill_id());
            pillNameValue = pillUa.getOriginal_name();

            TextView pillsDescription = findViewById(R.id.pillsDescription);
            pillsDescription.setText(pillUa.getDosage_form());
            pillName.setHint( Integer.toString(pillUa.getId()) );
        }
        pillName.setText( pillNameValue );

        pillName.setThreshold(3);//return 3 chars for starting autocomplete

        CronManager cronManager = new CronManager();
        Spinner cronTypeInput = findViewById(R.id.cron_type);
        int cronTypePosition = cronManager.getTypePosition( pillRule.getCron_type() );
        cronTypeInput.setSelection(cronTypePosition);

        Spinner cronIntervalInput = findViewById(R.id.cron_interval);
        int cronIntervalPosition = cronManager.getIntervalPosition( pillRule.getCron_interval() );
        cronIntervalInput.setSelection(cronIntervalPosition);

        //init inputs for time

        List<String> timeList = PillsDateTimeLists.getTimeList(pillRule);
        clearTimeInputs();
        generateTimeInputs(timeList);

        TextView startDateInput = findViewById(R.id.startDate);
        String startDateFormatted = Converters.dbDateToViewDate(pillRule.getStart_date());
        startDateInput.setText( startDateFormatted );

        TextView endDateInput = findViewById(R.id.endDate);
        String endDateFormatted = Converters.dbDateToViewDate(pillRule.getEnd_date());
        endDateInput.setText( endDateFormatted );

        if ( pillRule.getIs_continues() == 1){
            RadioButton dContinuous = findViewById(R.id.dContinuous);
            dContinuous.setChecked(true);
        }

        RadioButton frequencyButton = findViewById(R.id.fEveryDay);//default value
        switch ( pillRule.getFrequency_type() ) {
            case 1:
                frequencyButton = findViewById(R.id.fEveryDay);
                break;
            case 2:
                frequencyButton = findViewById(R.id.fDaysOfWeek);
                break;
            case 3:
                frequencyButton = findViewById(R.id.fDaysInterval);
                break;
        }
        frequencyButton.setChecked(true);
    }


    public void managePills(View view) throws ParseException {
        AutoCompleteTextView pillName = findViewById(R.id.pills);
        Spinner cronTypeInput = findViewById(R.id.cron_type);
        Spinner cronIntervalInput = findViewById(R.id.cron_interval);
        TextView startDateInput = findViewById(R.id.startDate);
        TextView endDateInput = findViewById(R.id.endDate);

        RadioGroup durationGroup = findViewById(R.id.duration_group);
        RadioGroup frequencyGroup = findViewById(R.id.frequency_group);

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

        String pillNameValue = pillName.getText().toString();
        pillRule.setName( pillNameValue );

        if ( pillName.getHint() != null){
            String pillIdString = String.valueOf(pillName.getHint());
            pillRule.setPill_id( Integer.parseInt(pillIdString) );
        }

        CronManager cronManager = new CronManager();
        Integer cronType = cronManager.getTypeByPosition(cronTypeInput.getSelectedItemPosition() );
        pillRule.setCron_type(cronType);

        Integer cronInterval = cronManager.getIntervalByPosition(cronIntervalInput.getSelectedItemPosition() );
        pillRule.setCron_interval(cronInterval);

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

        int durationGroupSelected = durationGroup.getCheckedRadioButtonId();
        RadioButton durationSelected = findViewById(durationGroupSelected);
        String durationSelectedName = durationSelected.getResources().getResourceEntryName(durationSelected.getId());

        int isContinuous = 0;
        if ( durationSelectedName.equals("dContinuous")){
            isContinuous = 1;
        }
        pillRule.setIs_continues(isContinuous);

        int frequencyGroupSelected = frequencyGroup.getCheckedRadioButtonId();
        RadioButton frequencyButton = findViewById(frequencyGroupSelected);
        String frequencyButtonName = frequencyButton.getResources().getResourceEntryName(frequencyButton.getId());
        int frequencyType = cronManager.getFrequencyTypeByLabel( frequencyButtonName );
        pillRule.setFrequency_type(frequencyType);

        //todo: make frequency values

        //validate and save
        if (validatePillRule( pillRule)){
            if ( updateRow ){
                localDatabase.localDAO().updateRule(pillRule);
            } else {
                pillRule.setCreated_at( curDateFormatted );
                long insertId = localDatabase.localDAO().addRule(pillRule);
                pillRule.setId((int) insertId);
            }

            //success saved, redirect to index page with list
            new PillTaskManager( pillRule.getId(), getApplicationContext());
            Toast.makeText(getApplicationContext(), R.string.message_data_saved, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PillsActivity.this,MainActivity.class));
        }

    }

    /**
     * Validate pill rule form
     * @param pillRule
     * @return boolean
     */
    protected boolean validatePillRule(PillRule pillRule){
        if (pillRule.getName().length() < 1){
            Toast.makeText(getApplicationContext(), R.string.novalid_pillrule_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
