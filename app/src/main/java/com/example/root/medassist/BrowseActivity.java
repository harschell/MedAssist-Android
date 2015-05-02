package com.example.root.medassist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.root.medassist.adapter.CustomListAdapter;
import com.example.root.medassist.app.AppController;
import com.example.root.medassist.model.Doctor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BrowseActivity extends Activity {
    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Movies json url
    private static final String url = "http://medassistdb.hostingsiteforfree.com/docs2.json";
    private ProgressDialog pDialog;
    private List<Doctor> docList = new ArrayList<Doctor>();
    private ListView listView;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, docList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();


        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Doctor doc = new Doctor();
                                doc.setTitle(obj.getString("names"));
                                doc.setThumbnailUrl("http://medassistdb.hostingsiteforfree.com/medLogo.jpg");
                                doc.setRating(((Number) obj.get("rating"))
                                        .doubleValue());
                                doc.setConsultation(obj.getInt("fee"));
                                doc.setAddress(obj.getString("address"));
                                //doc.setPhoneNum(obj.getString("phones"));
                                JSONArray phones = obj.getJSONArray("phones");
                                String phoneNum="";
                                for(int j =0;j<phones.length();j++)
                                {
                                    String num= (String)phones.get(j);
                                    num = "{"+num+"}";
                                    JSONObject phone = new JSONObject(num);
                                    Log.d("Browse",phone.toString());
                                    phoneNum+="+"+String.valueOf(phone.get("tel"));
                                    if(j<phones.length()-1)
                                        phoneNum+=",";
                                }
                                doc.setPhoneNum(phoneNum);
                                doc.setRegistered(obj.getInt("verify"));
                                doc.setSpeciality("General Physician");

                                // Genre is json array
                                /*JSONArray specialArry = obj.getJSONArray("speciality");
                                ArrayList<String> speciality = new ArrayList<String>();
                                for (int j = 0; j < specialArry.length(); j++) {
                                    speciality.add((String) specialArry.get(j));
                                }
                                doc.setSpeciality(speciality);*/
                                //doc.setSpeciality(obj.getString("speciality"));

                                // adding movie to movies array
                                docList.add(doc);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
        registerClickCallback();
    }
    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {

                Doctor clickedDoc = docList.get(position);
                //String message = "You clicked position" + position
                //        + "where Doctor name is" + clickedDoc.getTitle();
                //Toast.makeText(BrowseActivity.this, message, Toast.LENGTH_LONG).show();
                Intent checkDocIntent = new Intent(BrowseActivity.this,CheckDocActivity.class);
                checkDocIntent.putExtra("title",clickedDoc.getTitle());
                checkDocIntent.putExtra("url",clickedDoc.getThumbnailUrl());
                checkDocIntent.putExtra("address","Address : " + clickedDoc.getAddress());
                checkDocIntent.putExtra("consultation","Consultation Fee: " + String.valueOf(clickedDoc.getConsultation()));
                checkDocIntent.putExtra("rating","Rating : " + String.valueOf(clickedDoc.getRating()));
                checkDocIntent.putExtra("phoneNum",clickedDoc.getPhone());
                checkDocIntent.putExtra("speciality","Speciality : " + clickedDoc.getSpeciality());
                checkDocIntent.putExtra("verify",clickedDoc.getRegistration());
                startActivity(checkDocIntent);
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}