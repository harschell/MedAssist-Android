package com.example.root.medassist;

import com.example.root.medassist.app.AppConfig;
import com.example.root.medassist.app.AppController;
import com.example.root.medassist.helper.SessionManager;
import com.example.root.medassist.helper.SQLiteHandler;
import com.example.root.medassist.LoginActivity;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class RegisterDocActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private Button btnRegisterUser;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputmobNum;
    private EditText inputAddress;
    private EditText inputFee;
    private EditText inputTimings;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_doc);

        inputFullName = (EditText) findViewById(R.id.nameDoc);
        inputEmail = (EditText) findViewById(R.id.emailDoc);
        inputPassword = (EditText) findViewById(R.id.passwordDoc);
        inputmobNum = (EditText) findViewById(R.id.mobNumDoc);
        inputAddress = (EditText) findViewById(R.id.addressDoc);
        inputFee = (EditText) findViewById(R.id.feeDoc);
        inputTimings = (EditText) findViewById(R.id.timingDoc);

        btnRegister = (Button) findViewById(R.id.btnRegisterDoc);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreenDoc);
        btnRegisterUser = (Button) findViewById(R.id.RegisterUser);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterDocActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String mobNum = inputmobNum.getText().toString();
                String address = inputAddress.getText().toString();
                String fee = inputFee.getText().toString();
                String timing = inputTimings.getText().toString();
                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !mobNum.isEmpty() &&!address.isEmpty() && !fee.isEmpty() &!timing.isEmpty()) {
                    registerDoc(name, email, password,mobNum,address,fee,timing);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        //Link to Register User Activity
        btnRegisterUser.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerDoc(final String name, final String email,
                              final String password, final String mobNum, final String address, final String fee, final String timing) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL

                        //checkLogin(email, password);

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterDocActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
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
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "registerDoc");
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("mobNum", mobNum);
                params.put("address", address);
                params.put("fee",fee);
                params.put("timing",timing);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}