package com.pillsgt.pgt.managers;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pillsgt.pgt.databases.InitDatabases;
import com.pillsgt.pgt.databases.LocalDatabase;
import com.pillsgt.pgt.models.Doc;
import com.pillsgt.pgt.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class DocsManager {
    protected String TAG = "DocsManager";
    protected Context mContext;
    private LocalDatabase localDatabase;



    public void apiStartRequest(Context sContext, String Url){

        this.mContext = sContext;
        initDatabases();

        RequestQueue queue = Volley.newRequestQueue(mContext);
        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET,
                Url, null, reqSuccessListener(), reqErrorListener());
        queue.add(myReq);
    }

    private Response.Listener<JSONObject> reqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    saveDoc("confidence", response.getString("confidence") );
                    saveDoc("terms", response.getString("terms") );
                } catch (JSONException e) {
                    Log.e(TAG, "Unknown error in reqSuccessListener: "+e.getMessage());
                }
            }
        };
    }

    private Response.ErrorListener reqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.getMessage() );
            }
        };
    }

    protected void initDatabases(){
        localDatabase = InitDatabases.buildLocalDatabase(mContext);

    }

    protected void saveDoc(String alias, String rDoc) {
        if (rDoc.length() <= 0) {
            return;
        }

        Doc doc;
        doc = localDatabase.localDAO().loadDocByAlias(alias);
        if ( doc == null ){
            doc = new Doc();
            doc.setAlias(alias);
            doc.setDocument( rDoc );
            localDatabase.localDAO().addDoc(doc);
        } else {
            doc.setDocument( rDoc );
            localDatabase.localDAO().updateDoc(doc);
        }
    }

}
