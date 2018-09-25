package com.pillsgt.pgt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.NumberPicker;

import java.text.ParseException;

public class NumOfDaysFragment extends DialogFragment {

    public interface NodInterface {
        void onNumOfDaysChoose(int numDays);
    }

    private NodInterface mListener;

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (NodInterface) context;
        } catch(Exception e){
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final NumberPicker numberPicker = new NumberPicker(getActivity());

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(60);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.alert_dialog_choose_value);
        builder.setMessage(R.string.alert_dialog_choose_number);
        builder.setView(numberPicker);
        builder.setIcon(R.drawable.logo_red);

        builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onNumOfDaysChoose(numberPicker.getValue());
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onNumOfDaysChoose(-1);
            }
        });
        return builder.create();
    }

}
