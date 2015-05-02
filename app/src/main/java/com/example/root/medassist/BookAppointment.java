package com.example.root.medassist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

public class BookAppointment extends Activity {
    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();
    final Context context = this;
    private ProgressDialog pDialog;
    private CalendarView cal;
    String date;
    String timing;
    ImageButton appointmentFix;
    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookappointment);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
      //  ImageLoader imageLoader = AppController.getInstance().getImageLoader();
       // if (imageLoader == null)
        //    imageLoader = AppController.getInstance().getImageLoader();
        //NetworkImageView thumbNail = (NetworkImageView) findViewById(R.id.thumbnail);
        //thumbNail.setImageUrl(getIntent().getExtras().getString("url"), imageLoader);
        appointmentFix = (ImageButton)findViewById(R.id.appointment);
        final TextView title = (TextView)findViewById(R.id.title);
        title.setText(getIntent().getExtras().getString("title"));
        cal = (CalendarView) findViewById(R.id.calendarView);

        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                month+=1;
                Toast.makeText(getBaseContext(), "Selected Date is\n\n"
                                + dayOfMonth + " : " + month + " : " + year,
                        Toast.LENGTH_LONG).show();
                if(dayOfMonth<=9 && month<10)
                    date = year + ":" + "0"+month + ":" + "0"+dayOfMonth;
                else if(dayOfMonth<=9)
                    date = year + ":" + month + ":" + "0"+dayOfMonth;
                else if(month<=9)
                    date = year + ":" + "0"+month + ":" + dayOfMonth;
                else
                    date = year + ":" + month + ":" + dayOfMonth;

                Log.e("Date",date);
                //System.out.println(date);
            }
        });
        appointmentFix.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                /*Intent appointmentIntent = new Intent(getApplicationContext(),
                        BookAppointment.class);
                appointmentIntent.putExtra("title", title.getText());
                appointmentIntent.putExtra("address",address.getText());
                appointmentIntent.putExtra("phoneNum", phoneNum.getText());
                appointmentIntent.putExtra("url",getIntent().getExtras().getString("url"));
                startActivity(appointmentIntent);*/
                //registerAppointment();
                HashMap<String, String> user = db.getUserDetails();

                String name = user.get("name");
                String email = user.get("email");
                String docName = getIntent().getExtras().getString("title");
                String docNum = getIntent().getExtras().getString("phoneNum");
                String docAddress = getIntent().getExtras().getString("address");
                Log.d("Going to Register",name + " " + email);
                registerAppointment(name, email, docName, docNum, docAddress, date, timing);
                Log.d("Date",date);
                /*Show up a dialog box for notifying*/
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    // set title
                    alertDialogBuilder.setTitle("Confirmation");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Your appointment is confirmed! Thank you!")
                            .setCancelable(false)
                            .setPositiveButton("Return to Home",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    Intent mainActivity = new Intent(getApplicationContext(),
                                            MainActivity.class);
                                    startActivity(mainActivity);
                                    finish();
                                }
                            })
                            .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

            }
        });
    }
    private void registerAppointment(final String patientName, final String patientEmail,final String docName, final String docNum, final String docAddress,
                                     final String date, final String timing) {
        // Tag used to cancel the request
        String tag_string_req = "req_storeAppointment";

        //pDialog.setMessage("Registering ...");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Appointment Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL

                        //checkLogin(email, password);

                        // Launch login activity
                        /*Intent intent = new Intent(
                                RegisterDocActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();*/
                        Log.d("Successful store","Yess!!!");
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration of Appointment Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "storeAppointment");
                params.put("patientName", patientName);
                params.put("patientEmail", patientEmail);
                params.put("docName", docName);
                params.put("docNum", docNum);
                params.put("docAddress", docAddress);
                params.put("date",date);
                params.put("timing",timing);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void onRadioButtonClick(View v) {
        RadioButton button = (RadioButton) v;
        Toast.makeText(BookAppointment.this,
                button.getText() + " was chosen.",
                Toast.LENGTH_SHORT).show();
        timing = String.valueOf(button.getText());
    }
}