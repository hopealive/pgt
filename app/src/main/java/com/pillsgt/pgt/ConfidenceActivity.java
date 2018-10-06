package com.pillsgt.pgt;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pillsgt.pgt.databases.LocalDatabase;
import com.pillsgt.pgt.models.Doc;
import com.pillsgt.pgt.utils.Utils;

public class ConfidenceActivity extends AppCompatActivity {

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

    protected void initDoc() {
        LocalDatabase localDatabase = Room.databaseBuilder(getApplicationContext(), LocalDatabase.class, Utils.localDbName)
                .allowMainThreadQueries()
                .build();
        Doc doc = localDatabase.localDAO().loadDocByAlias("confidence");
        if ( doc != null ){
Log.d ("TAGConfA", doc.getDocument() );//todo: remove
Log.d ("TAGConfA", doc.getAlias() );//todo: remove
            TextView document = findViewById(R.id.document);
            document.setText(Html.fromHtml(doc.getDocument()));
        }
    }

}
