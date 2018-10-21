package com.pillsgt.pgt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.pillsgt.pgt.models.Doc;

public class ConfidenceActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confidence);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initDoc();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ConfidenceActivity.this,MainActivity.class));
                Snackbar.make(view, "Loading...", Snackbar.LENGTH_LONG)
                        .setAction("Back", null).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(ConfidenceActivity.this,MainActivity.class));
    }

    protected void initDoc() {
        Doc doc = localDatabase.localDAO().loadDocByAlias("confidence");
        if ( doc != null ){
            TextView document = findViewById(R.id.document);
            document.setText(Html.fromHtml(doc.getDocument()));
        }
    }

}
