package com.pillsgt.pgt.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.pillsgt.pgt.R;


public class PillsTimeInputFragment extends Fragment {

    public static PillsTimeInputFragment newInstance(int inputId, String timeValue) {
        PillsTimeInputFragment pillsTimeInputFragment = new PillsTimeInputFragment();
        Bundle args = new Bundle();
        args.putInt("inputId", inputId);
        args.putString("timeValue", timeValue);
        pillsTimeInputFragment.setArguments(args);
        return pillsTimeInputFragment;
    }

    protected int inputId = 0;
    protected int nInputId = 0;
    protected String timeValue = "12:00";//default value
    private int mHour, mMinute;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputId = getArguments().getInt("inputId", inputId);
        nInputId = 1000+inputId;

        timeValue = getArguments().getString("timeValue", timeValue);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pills_time_input_fragment, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        TextView ptiTime = getActivity().findViewById(R.id.pti_time);
        ptiTime.setId(nInputId);
        ptiTime.setText(timeValue);
        ptiTime.setOnClickListener(ptiListener);
    }

    View.OnClickListener ptiListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Get time from inputs
            String[] timeValues = timeValue.split(":");
            mHour = Integer.parseInt(timeValues[0]);
            mMinute = Integer.parseInt(timeValues[1]);

            //Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    TextView ptiTime = getActivity().findViewById(nInputId);
                    String timeString = "";
                    if ( hourOfDay < 10){
                        timeString += "0";
                    }
                    timeString += hourOfDay + ":";

                    if (minute < 10){
                        timeString += "0";
                    }
                    timeString += minute;

                    ptiTime.setText(timeString);
                }
            }, mHour, mMinute, true);
            timePickerDialog.show();
        }
    };

}
