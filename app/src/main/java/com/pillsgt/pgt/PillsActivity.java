package com.pillsgt.pgt;

import android.app.DatePickerDialog;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.pillsgt.pgt.managers.keywordautocomplete.PillsAutoCompleteTextChangedListener;
import com.pillsgt.pgt.managers.keywordautocomplete.PillsAutoCompleteView;
import com.pillsgt.pgt.managers.CronManager;
import com.pillsgt.pgt.managers.PillTaskManager;
import com.pillsgt.pgt.models.PillRule;
import com.pillsgt.pgt.models.PillTimeRule;
import com.pillsgt.pgt.models.remote.PillsUa;
import com.pillsgt.pgt.utils.Converters;
import com.pillsgt.pgt.utils.PillsDateTimeLists;
import com.pillsgt.pgt.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class PillsActivity extends AppActivity implements
        NumOfDaysFragment.NodInterface {

    //pill rule data
    protected Integer pillRuleId = null;
    protected PillRule pillRule;
    protected PillRule oPillRule;
    protected List<PillTimeRule> pillTimeRules;

    //inputs
    public static PillsAutoCompleteView pillsAutoComplete;
    public TextView pillsDescription;
    public ArrayAdapter<String> myAdapter;
    public String[] autoCompleteListItems = new String[] {"..."};
    RadioGroup durationGroup;


    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();

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

        String sPillRuleId = getIntent().getStringExtra("pillRuleId");

        pillRuleId = null;
        pillRule = new PillRule();
        oPillRule = new PillRule();
        if ( sPillRuleId != null ){
            pillRuleId = Integer.valueOf(sPillRuleId);
            pillRule = localDatabase.localDAO().loadRuleById(pillRuleId);
            oPillRule = localDatabase.localDAO().loadRuleById(pillRuleId);
            pillTimeRules = localDatabase.localDAO().loadTimeRulesByRuleId(pillRule.getId());
        }

        initControls();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PillsActivity.this,MainActivity.class));
    }


    protected void initControls(){
        initPillName();
        initCronType();
        initCronInterval();

        setInitialDate();
        setInitialSchedule();

        initEdit();
    }

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

    /**
     * BLOCK FOR WORKING WITH TIME PARAMS: cron type and cron interval
     */
    protected void initCronType(){
        Spinner spinner = findViewById(R.id.cron_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cron_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(cronTypeListener);
    }

    Spinner.OnItemSelectedListener cronTypeListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            mapPillRuleCronValues();
            List<String> timeList;
            if ( oPillRule.getCron_type() == pillRule.getCron_type()){
                timeList = PillsDateTimeLists.getTimeList(pillRule, pillTimeRules);
            } else {
                timeList = PillsDateTimeLists.getTimeList(pillRule, null);
            }
            generateTimeInputs(timeList);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    };

    protected void initCronInterval(){
        Spinner spinner = findViewById(R.id.cron_interval);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cron_interval, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(cronIntervalListener);
    }

    Spinner.OnItemSelectedListener cronIntervalListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            mapPillRuleCronValues();
            List<String> timeList;
            if ( oPillRule.getCron_interval() == pillRule.getCron_interval()){
                timeList = PillsDateTimeLists.getTimeList(pillRule, pillTimeRules);
            } else {
                timeList = PillsDateTimeLists.getTimeList(pillRule, null);
            }
            generateTimeInputs(timeList);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) { }
    };

    /**
     * List is array, which ite, is time string like "12:00"
     * Insert fragmetns with timepickers
     * @param list
     */
    protected void generateTimeInputs(List<String> list) {
        try {
            //remove old inputs
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            if (fragmentList != null) {
                for (Fragment frag : fragmentList )
                {
                    if (frag.getTag().equals("fragmentTimeInputs")) {
                        getSupportFragmentManager().beginTransaction().remove(frag).commit();
                        continue;
                    }
                }
            }

            //add new items
            int i = 1;
            for (String item : list){
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .add(R.id.time_input_fragment, PillsTimeInputFragment.newInstance(i, item), "fragmentTimeInputs")
                        .commit();
                fm.executePendingTransactions();
                ++i;
            }

        } catch (Exception e){
            e.printStackTrace();
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

    protected void initEdit(){
        Button submitButton = findViewById(R.id.manage_pills);
        if(pillRuleId == null ){
            submitButton.setText( R.string.button_add );
            return;
        }
        submitButton.setText( R.string.button_edit );


        //fill data by id pull rule

        //fill name
        AutoCompleteTextView pillNameInput = findViewById(R.id.pills);
        pillNameInput.setThreshold(Integer.MAX_VALUE);//hook

        String pillNameValue = pillRule.getName();
        if ( pillRule.getPill_id() > 0 ){
            PillsUa pillUa = remoteDatabase.remoteDAO().loadPillsUa(pillRule.getPill_id());
            pillNameValue = pillUa.getOriginal_name();

            TextView pillsDescription = findViewById(R.id.pillsDescription);
            pillsDescription.setText(pillUa.getDosage_form());
            pillNameInput.setHint( Integer.toString(pillUa.getId()) );
        }
        pillNameInput.setText( pillNameValue );
        pillNameInput.setThreshold(3);//return 3 chars for starting autocomplete

        //fill cron selectboxes
        CronManager cronManager = new CronManager();
        Spinner cronTypeInput = findViewById(R.id.cron_type);
        int cronTypePosition = cronManager.getTypePosition( pillRule.getCron_type() );
        cronTypeInput.setSelection(cronTypePosition);

        Spinner cronIntervalInput = findViewById(R.id.cron_interval);
        int cronIntervalPosition = cronManager.getIntervalPosition( pillRule.getCron_interval() );
        cronIntervalInput.setSelection(cronIntervalPosition);

        //set time input values
        List<String> timeList = PillsDateTimeLists.getTimeList(pillRule, pillTimeRules);
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
        AutoCompleteTextView pillNameInput = findViewById(R.id.pills);
        TextView startDateInput = findViewById(R.id.startDate);
        TextView endDateInput = findViewById(R.id.endDate);

        RadioGroup durationGroup = findViewById(R.id.duration_group);
        RadioGroup frequencyGroup = findViewById(R.id.frequency_group);

        //add to db
        Boolean updateRow = false;
        if ( pillRuleId != null){
            updateRow = true;
        }

        String pillNameValue = pillNameInput.getText().toString();
        pillRule.setName( pillNameValue );

        if ( pillNameInput.getHint() != null){
            String pillIdString = String.valueOf(pillNameInput.getHint());
            pillRule.setPill_id( Integer.parseInt(pillIdString) );
        }

        //setting cron values: setCron_type, setCron_interval
        mapPillRuleCronValues();

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

        CronManager cronManager = new CronManager();
        int frequencyType = cronManager.getFrequencyTypeByLabel( frequencyButtonName );
        pillRule.setFrequency_type(frequencyType);

        //todo: make frequency values

        //validate and save
        if (validatePillRule( pillRule)){
            try {
                //save rule
                if ( updateRow ){
                    localDatabase.localDAO().updateRule(pillRule);
                } else {
                    pillRule.setCreated_at( curDateFormatted );
                    long insertId = localDatabase.localDAO().addRule(pillRule);
                    pillRule.setId((int) insertId);
                }

                //save rule times for new
                saveTimeInputs(pillRule);
            } catch (Exception e){
                e.printStackTrace();
            }

            //success saved, redirect to index page with list
            new PillTaskManager( pillRule.getId(), getApplicationContext());
            Toast.makeText(getApplicationContext(), R.string.message_data_saved, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PillsActivity.this,MainActivity.class));
        }
    }


    protected void saveTimeInputs(PillRule pillRule){
        List<String> timeList = PillsDateTimeLists.getTimeList(pillRule, pillTimeRules);

        try {
            localDatabase.localDAO().deletePillTimeRuleByRuleId(pillRule.getId());
            for(int i=0; i<timeList.size(); i++){
                TextView timeInput = findViewById(1001 + i);

                PillTimeRule pillTimeRule = new PillTimeRule();
                pillTimeRule.setRule_id(pillRule.getId());
                pillTimeRule.setAlarm_at( String.valueOf(timeInput.getText()) );

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdFormat = new SimpleDateFormat(Utils.dateTimePatternDb );
                sdFormat.setTimeZone( TimeZone.getTimeZone("GMT") );
                String curDateFormatted = sdFormat.format(calendar.getTime());
                pillTimeRule.setCreated_at(curDateFormatted);
                pillTimeRule.setUpdated_at(curDateFormatted);

                localDatabase.localDAO().addPillTimeRule(pillTimeRule);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void mapPillRuleCronValues(){
        CronManager cronManager = new CronManager();

        Spinner cronTypeInput = findViewById(R.id.cron_type);
        Integer cronType = cronManager.getTypeByPosition(cronTypeInput.getSelectedItemPosition() );
        pillRule.setCron_type(cronType);

        Spinner cronIntervalInput = findViewById(R.id.cron_interval);
        Integer cronInterval = cronManager.getIntervalByPosition(cronIntervalInput.getSelectedItemPosition() );
        pillRule.setCron_interval(cronInterval);
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
