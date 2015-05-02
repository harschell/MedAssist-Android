package com.example.root.medassist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.root.medassist.app.AppController;

public class CheckDocActivity extends Activity {
    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkdoc);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        final ImageButton appointmentFix = (ImageButton)findViewById(R.id.appointment);
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView)findViewById(R.id.thumbnail);
        thumbNail.setImageUrl(getIntent().getExtras().getString("url"), imageLoader);
        final TextView title = (TextView)findViewById(R.id.title);
        TextView rating = (TextView)findViewById(R.id.rating);
        TextView speciality = (TextView)findViewById(R.id.speciality);
        TextView consultation = (TextView)findViewById(R.id.consultation);
        final TextView phoneNum = (TextView)findViewById(R.id.phoneNum);
        final TextView address = (TextView)findViewById(R.id.address);
        title.setText(getIntent().getExtras().getString("title"));
        rating.setText(getIntent().getExtras().getString("rating"));
        speciality.setText(getIntent().getExtras().getString("speciality"));
        consultation.setText(getIntent().getExtras().getString("consultation"));

        phoneNum.setText("Phone Number:" + getIntent().getExtras().getString("phoneNum"));
        Log.d("CHeckDoc","PhoneNum:"+phoneNum.getText());

        address.setText(getIntent().getExtras().getString("address"));
        Log.e("VERIFICATION",getIntent().getExtras().getString("verify"));
        if(getIntent().getExtras().getString("verify").equals("No"))
        {
            appointmentFix.setVisibility(View.INVISIBLE);
        }
        hidePDialog();
        appointmentFix.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent appointmentIntent = new Intent(getApplicationContext(),
                        BookAppointment.class);
                appointmentIntent.putExtra("title", title.getText());
                appointmentIntent.putExtra("address",address.getText());
                appointmentIntent.putExtra("phoneNum", phoneNum.getText());
                appointmentIntent.putExtra("url",getIntent().getExtras().getString("url"));
                startActivity(appointmentIntent);
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