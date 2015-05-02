package com.example.root.medassist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.root.medassist.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class ViewPrescription extends Activity implements OnItemSelectedListener {
    Spinner spinner1;
    TextView prescription;
    TextView selVersion;
    SQLiteHandler db;
    Button submit;
    Button review;
    final Context context = this;
    private Spinner qspinner1;
    private Spinner qspinner2;
    private Spinner qspinner3;
    private Spinner qspinner4;
    private Spinner qspinner5;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_prescription);
        db = new SQLiteHandler(getApplicationContext());
        final String[] state = {"Cupcake", "Donut", "Eclair", "Froyo",
                "Gingerbread", "HoneyComb", "IceCream Sandwich", "Jellybean",
                "kitkat"};
        System.out.println(state.length);
        List<String> docs = getDocs();
        selVersion = (TextView) findViewById(R.id.selState);
        prescription = (TextView) findViewById(R.id.prescription);
        submit = (Button) findViewById(R.id.Submit);
        review = (Button) findViewById(R.id.review);
        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, docs);
        adapter_state
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setAdapter(adapter_state);
        spinner1.setOnItemSelectedListener(this);
        submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setPrescription((String) spinner1.getSelectedItem());
                review.setVisibility(View.VISIBLE);
            }
        });

        review.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // get prompts.xml view
                final JSONObject presData = new JSONObject();

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.reviewdialog, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setPositiveButton("Submit Review", null);
                alertDialogBuilder.setNegativeButton("Cancel", null);
                alertDialogBuilder.setTitle("Review for "+(String)spinner1.getSelectedItem());
                final AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
                String[] ratings = {"0","1","2","3","4"};
                //presButton = (Button) promptsView.findViewById(R.id.presButton);
                qspinner1 = (Spinner) promptsView.findViewById(R.id.spinner1);
                qspinner2 = (Spinner) promptsView.findViewById(R.id.spinner2);
                qspinner3 = (Spinner) promptsView.findViewById(R.id.spinner3);
                qspinner4 = (Spinner) promptsView.findViewById(R.id.spinner4);
                qspinner5 = (Spinner) promptsView.findViewById(R.id.spinner5);
                // set dialog message
                alertDialog.setCancelable(false);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        /*int r1 = Integer.parseInt((String)qspinner1.getSelectedItem());
                        int r2 = Integer.parseInt((String)qspinner2.getSelectedItem());
                        int r3 = Integer.parseInt((String)qspinner3.getSelectedItem());
                        int r4 = Integer.parseInt((String)qspinner4.getSelectedItem());
                        int r5 = Integer.parseInt((String)qspinner5.getSelectedItem());
                        double avg = (r1+r2+r3+r4+r5)/5;*/
                        System.out.println("Average of Ratings:3");
                        alertDialog.dismiss();
                    }
                });
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
            }
        });
    }
    public List<String> getDocs()
    {
        LinkedHashMap<String,String> gotDocs = db.getDocs();
        List<String> docs = new ArrayList<String>();
        Set<String> keys = gotDocs.keySet();
        for(String k:keys){
            docs.add(gotDocs.get(k));
        }
        return docs;
    }
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        spinner1.setSelection(position);
        String selState = (String) spinner1.getSelectedItem();
        selVersion.setText("Selected Doctor:" + selState);
    }

    @Override

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    public void setPrescription(String DocName) {
        JSONObject msgText = db.getDocAppointment(DocName);
        String displayMessage = "Your Current Prescription\n\n";

        try {
            displayMessage += "Date:" + msgText.getString("Date") + "\n";
            displayMessage += "Doctor:" + msgText.getString("DocName") + "\n";
            String name = "Patient Name:" + msgText.getString("Patient Name");
            displayMessage += name + "\n";
            displayMessage += "Age:" + msgText.getString("Patient Age") + "\n";
            displayMessage += "\nMedicines\n";
            String meds = "";
            String dosage = "";
            String days = "";

            JSONArray medicines = msgText.getJSONArray("Medicines");
            for (int i = 1; i <= medicines.length(); i++) {
                JSONObject currentMed = (JSONObject) medicines.get(i - 1);
                meds = currentMed.getString("Medicine Name");
                dosage = currentMed.getString("Dosage");
                days = currentMed.getString("Number of Days");
                displayMessage += "Medicine " + String.valueOf(i) + ":\n";
                displayMessage += "Name:" + meds + "\n";
                displayMessage += "Dosage:" + dosage + "\n";
                displayMessage += "Number of Days:" + days + "\n";
                displayMessage += "\n";
            }
            displayMessage += "Thank you!";
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        prescription.setText(displayMessage);
        prescription.setVisibility(View.VISIBLE);
    }
}