package com.pillsgt.pgt.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    protected String timeValue = "12:00";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputId = getArguments().getInt("inputId", 0);
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
        ptiTime.setId(ptiTime.getId()+inputId);
        ptiTime.setText(timeValue);

        ptiTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(),"TimePicker");
            }
        });
    }

}
