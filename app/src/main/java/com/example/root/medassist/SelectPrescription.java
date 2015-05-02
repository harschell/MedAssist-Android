package com.example.root.medassist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.root.medassist.helper.SQLiteHandler;
import com.example.root.medassist.helper.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SelectPrescription extends Activity {

    LinearLayout linearMain;
    CheckBox checkBox;
    private SQLiteHandler db;
    private SessionManager session;
    private HashMap<String,String> selectedDocs;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectprescriptions);
        linearMain = (LinearLayout) findViewById(R.id.linearLayout);
        selectedDocs = new HashMap<>();
        db = new SQLiteHandler(getApplicationContext());
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        StartAppointmentActivity.class);
                ArrayList<String> docs= new ArrayList<>(selectedDocs.values());
                i.putExtra("SelectedDocs",docs);
                startActivity(i);
            }
        });
        LinkedHashMap<String, String> docs = db.getDocs();
        Set<?> set = docs.entrySet();
        Iterator<?> i = set.iterator();
        while (i.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry me = (Map.Entry) i.next();
            System.out.print(me.getKey() + ": ");
            System.out.println(me.getValue());
            checkBox = new CheckBox(this);
            checkBox.setId(Integer.parseInt(me.getKey().toString()));
            checkBox.setText(me.getValue().toString());
            checkBox.setTextSize(18f);
            checkBox.setOnClickListener(getOnClickDoSomething(checkBox));
            linearMain.addView(checkBox);
        }


    }
    View.OnClickListener getOnClickDoSomething ( final Button button){
        return new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("*****id******" + button.getId());
                System.out.println("and text***" + button.getText().toString());
                if(selectedDocs.containsKey(button.getId()))
                {
                    selectedDocs.remove(button.getId());
                }
                else
                {
                    selectedDocs.put(String.valueOf(button.getId()),button.getText().toString());
                }
            }
        };
    }
}