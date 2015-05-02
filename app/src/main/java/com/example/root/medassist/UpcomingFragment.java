package com.example.root.medassist;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.root.medassist.app.AppConfig;
import com.example.root.medassist.app.AppController;
import com.example.root.medassist.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpcomingFragment extends ListFragment {
    // Progress Dialog
    private ProgressDialog pDialog;
    private static SQLiteHandler db;
    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();
    private String emailId="";
    ArrayList<HashMap<String, String>> appointmentList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_upcoming, container, false);
        // Hashmap for ListView
        db = new SQLiteHandler(getActivity());
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading Appointments ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        emailId = db.getUserDetails().get("email");
        appointmentList = new ArrayList<HashMap<String, String>>();

        final BaseAdapter adapter = new SimpleAdapter(
                getActivity().getApplicationContext(), appointmentList,
                R.layout.upcoming_list_item, new String[]{"docName", "docAddress", "date"},
                new int[]{R.id.from, R.id.subject, R.id.date});

        setListAdapter(adapter);

        String tag_string_req = "retrieve Appointments";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d("retrieving appointment", "Response: " + response.toString());
                //hideDialog();
                pDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        JSONObject appointments = jObj.getJSONObject("appointments");
                        //System.out.println(appointments.toString());
                        int count = Integer.parseInt(appointments.getString("count"));
                        for(int i=1;i<=count;i++)
                        {
                            JSONObject c = appointments.getJSONObject(String.valueOf(i));
                            // Storing each json item in variable

                            String date = c.getString("date");
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy:MM:dd");
                            final String CurrentDate = df.format(cal.getTime());
                            Log.d("Upcoming","Got Date" + date);
                            if(CurrentDate.compareTo(date) > 0)
                            {
                                continue;
                            }
                            String docName = c.getString("docName");
                            String docAddress = c.getString("docAddress");
                            String timing = c.getString("timing");
                            //Log.d("Upcoming",docAddress);
                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();
                            //System.out.println("id:"+i);
                            // adding each child node to HashMap key => value
                            map.put("date",date);
                            map.put("docName", docName);
                            map.put("docAddress", docAddress);
                            map.put("timing", timing);

                            // adding HashList to ArrayList
                            appointmentList.add(map);
                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        // Error in login. Get the error message
                            /*String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        */}
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Fragment", "Retrieval Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "getAppointments");
                params.put("email", emailId);
                //params.put("password", password);
                //params.put("mobNum", mobNum);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
		return rootView;
	}

}
