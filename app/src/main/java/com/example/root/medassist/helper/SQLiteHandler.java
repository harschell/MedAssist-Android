package com.example.root.medassist.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_LOGIN = "login";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_MOBNO = "mobNum";
    private static final String KEY_CREATED_AT = "created_at";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_MOBNO + " TEXT UNIQUE,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        String CREATE_APPOINTMENTS = "CREATE TABLE IF NOT EXISTS Appointments(date VARCHAR,docName VARCHAR,patientName VARCHAR,patientAge VARCHAR,"
                + "meds TEXT, dosage TEXT, days TEXT, PRIMARY KEY(date,docName,patientName))";
        db.execSQL(CREATE_APPOINTMENTS);
        String storeAppointments = "CREATE TABLE IF NOT EXISTS Upcoming(date VARCHAR,docName VARCHAR, docAddress VARCHAR, timing VARCHAR, PRIMARY KEY(date,docName))";
        db.execSQL(storeAppointments);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void storeAppointments(String date,String docName, String docAddress, String timing)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date",date);
        values.put("docName",docName);
        values.put("docAddress",docAddress);
        values.put("timing",timing);
        long id = db.insert("Upcoming",null,values);
        db.close();
        Log.d("Sql","Upcoming Appointment Inserted");
    }
     public void addUser(String name, String email, String uid,String mobNum, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_MOBNO,mobNum);//MObile Number
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }
    public void addAppointment(String date, String docName, String patient, String patientAge, String meds, String dosage, String days)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date",date);
        values.put("docName",docName);
        values.put("patientName",patient);
        values.put("patientAge",patientAge);
        values.put("meds",meds);
        values.put("dosage",dosage);
        values.put("days",days);
        long id = db.insert("Appointments",null,values);
        db.close();
        Log.d("Sql","Appointment Inserted");
    }
    /*public HashMap<String,String> getAppointments()
    {
        HashMap<String, String> appointment = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + "Appointments";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            appointment.put("date", cursor.getString(0));
            appointment.put("docName", cursor.getString(1));
            appointment.put("patientName", cursor.getString(2));
            appointment.put("meds",cursor.getString(3));
            appointment.put("dosage", cursor.getString(4));
            appointment.put("days",cursor.getString(5));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return appointment;
    }*/
    public LinkedHashMap<String,String> getDocs(){
        LinkedHashMap<String,String> docs = new LinkedHashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + "Appointments";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        int count = 1;
        while (cursor.isAfterLast() == false) {
            docs.put(String.valueOf(count),cursor.getString(1));
            cursor.moveToNext();
            count++;
        }
        cursor.close();
        db.close();
        return docs;
    }
    public JSONObject getUpcomingAppointments() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy:MM:dd");
        final String CurrentDate = df.format(c.getTime());
        System.out.println("Current Date" + CurrentDate);
        JSONObject appointments = new JSONObject();
        String selectQuery = "SELECT * FROM Upcoming";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        JSONArray allAppointment = new JSONArray();
        while (cursor.isAfterLast() == false) {
            String date = cursor.getString(0);
            System.out.println("got date:" + date);
            if (CurrentDate.compareTo(date) < 0) {
                try {
                    JSONObject currentAppointment = new JSONObject();
                    currentAppointment.put("date", cursor.getString(0));
                    currentAppointment.put("docName", cursor.getString(1));
                    currentAppointment.put("docAddress", cursor.getString(2));
                    currentAppointment.put("timing", cursor.getString(3));
                    allAppointment.put(currentAppointment);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
            cursor.moveToNext();
        }
        try {
            appointments.put("Appointments", allAppointment);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        cursor.close();
        db.close();
        return appointments;
    }
    public JSONObject getPastAppointments() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy:MM:dd");
        final String CurrentDate = df.format(c.getTime());
        System.out.println("Current Date" + CurrentDate);
        JSONObject appointments = new JSONObject();
        String selectQuery = "SELECT * FROM Upcoming";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        JSONArray allAppointment = new JSONArray();
        while (cursor.isAfterLast() == false) {
            String date = cursor.getString(0);
            System.out.println("got date:" + date);
            if (CurrentDate.compareTo(date) > 0) {
                try {
                    JSONObject currentAppointment = new JSONObject();
                    currentAppointment.put("date", cursor.getString(0));
                    currentAppointment.put("docName", cursor.getString(1));
                    currentAppointment.put("docAddress", cursor.getString(2));
                    currentAppointment.put("timing", cursor.getString(3));
                    allAppointment.put(currentAppointment);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
            cursor.moveToNext();
        }
        try {
            appointments.put("Appointments", allAppointment);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        cursor.close();
        db.close();
        return appointments;
    }
    public JSONObject getDocAppointment(String docName)
    {
        JSONObject presData = new JSONObject();
        String selectQuery = "SELECT  * FROM " + "Appointments WHERE docName = '" + docName + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        int count = 1;
        while (cursor.isAfterLast() == false) {
            //docs.put(String.valueOf(count),cursor.getString(1));
            try {
                System.out.println("Starting...");
                System.out.println(cursor.getString(5));
                presData.put("Sender","Past");
                presData.put("Date", cursor.getString(0));
                presData.put("DocName", cursor.getString(1));
                presData.put("Patient Name", cursor.getString(2));
                presData.put("Patient Age", cursor.getString(3));
                String meds[] = cursor.getString(4).split(" ");
                String dosage[] = cursor.getString(5).split(" ");
                System.out.println(dosage.length);
                String days[] = cursor.getString(6).split(" ");
                JSONArray Medicines = new JSONArray();
                System.out.println("Length"+meds.length);
                for (int i = 0; i < meds.length; i++) {
                    JSONObject medDetails = new JSONObject();
                    System.out.println(meds[i]);
                    System.out.println("i:"+i);
                    System.out.println(dosage[i]);
                    medDetails.put("Medicine Name", meds[i]);
                    medDetails.put("Dosage", dosage[i]);
                    medDetails.put("Number of Days", days[i]);
                    Medicines.put(medDetails);
                }
                presData.put("Medicines", Medicines);
                cursor.moveToNext();
                count++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();
        return presData;
    }
    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("mobNum",cursor.getString(4));
            user.put("created_at", cursor.getString(5));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Getting user login status return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        //db.delete("Appointments",null,null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
