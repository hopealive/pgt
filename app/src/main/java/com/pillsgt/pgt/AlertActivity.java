package com.pillsgt.pgt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AlertActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
    }


    public void saveAlert(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        //todo: save data
    }

    public void take(Integer ruleId) {
        //todo: save data
    }


    public void snooze(Integer ruleId) {
        //todo: save data
    }


    public void reschedule(Integer ruleId) {
        //todo: save data
    }
}
