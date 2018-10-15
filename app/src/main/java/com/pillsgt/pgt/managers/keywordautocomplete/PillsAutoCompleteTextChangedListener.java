package com.pillsgt.pgt.managers.keywordautocomplete;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.pillsgt.pgt.PillsActivity;
import com.pillsgt.pgt.databases.InitDatabases;
import com.pillsgt.pgt.databases.RemoteDatabase;
import com.pillsgt.pgt.models.remote.Keyword;
import com.pillsgt.pgt.models.remote.KeywordRelation;
import com.pillsgt.pgt.models.remote.PillsUa;

import java.util.ArrayList;
import java.util.List;

public class PillsAutoCompleteTextChangedListener implements TextWatcher{

    public static final String TAG = "PillsAutoListener";
    Context context;

    public PillsAutoCompleteTextChangedListener(Context context){
        this.context = context;
    }

    @Override
    public void afterTextChanged(Editable s) {
        PillsActivity mainActivity = ((PillsActivity) context);
        PillsAutoCompleteView pillsAutoComplete = mainActivity.pillsAutoComplete;
        String pillNameValue = String.valueOf(pillsAutoComplete.getText());

        if ( pillNameValue.contains(" | ") ) {
            String[] pillNameValues = pillNameValue.split(" \\| ");

            String pillOriginalId = "";
            if ( pillNameValues[2] != null && pillNameValues[2].contains("#") ) {
                pillOriginalId = pillNameValues[2].replace("#", "");
                pillsAutoComplete.setText(pillNameValues[0]);
                pillsAutoComplete.setHint(pillOriginalId);

                mainActivity.pillsDescription.setText(pillNameValues[1]);
            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Auto-generated method stub
    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {
        List<String> keywordIdsList = new ArrayList<String>();
        RemoteDatabase remoteDatabase = InitDatabases.buildRemoteDatabase( context );

        if ( userInput.toString().length() >= 3 ){
            List<Keyword> keywords = remoteDatabase.remoteDAO().searchKeywords( userInput.toString() );
            String keywordIdString;
            for (final Keyword keyword : keywords ) {
                keywordIdString = Integer.toString(keyword.getId());
                if ( !keywordIdsList.contains(keywordIdString) ){
                    keywordIdsList.add(keywordIdString);
                }
            }
        }

        if ( keywordIdsList.size() > 0 && keywordIdsList.size() < 999 ){
            List<KeywordRelation> keywordRelations = remoteDatabase.remoteDAO().getKeywordRelationsByKeywordId(keywordIdsList);
            List<String> pillIds = new ArrayList<String>();
            String originalIdString;

            for (final KeywordRelation keywordRelation : keywordRelations ){
                originalIdString = Integer.toString(keywordRelation.getOriginal_id());
                if (!pillIds.contains(originalIdString )){
                    pillIds.add( originalIdString  );
                }
            }

            PillsActivity mainActivity = ((PillsActivity) context);
            if ( pillIds.size() > 0 ){
                List<PillsUa> pillsUa = remoteDatabase.remoteDAO().loadPillsUaByIds(pillIds);

                String[] item = new String[pillsUa.size()];
                int i = 0;
                for (final PillsUa pillUa : pillsUa ){
                    item[i] = pillUa.getOriginal_name()
                        + " | " + pillUa.getDosage_form()
                        + " | #" + Integer.toString(pillUa.getId());
                    ++i;
                }
                mainActivity.item = item;

                // update the adapter
//                mainActivity.myAdapter.notifyDataSetChanged();//todo: think about this
                mainActivity.myAdapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_dropdown_item_1line, mainActivity.item);
                mainActivity.pillsAutoComplete.setAdapter(mainActivity.myAdapter);
            }
        }
    }

}