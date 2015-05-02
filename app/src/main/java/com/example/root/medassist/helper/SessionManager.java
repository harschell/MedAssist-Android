package com.example.root.medassist.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;
    //String loginUser;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String loginUser = "Patient";
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        //loginUser="";
    }
    /*public String getloginType()
    {
        return loginUser;
    }*/
    public void setLogin(boolean isLoggedIn,boolean Patient) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        //loginUser = user;
        // commit changes
        editor.putBoolean(loginUser,Patient);
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
    public boolean isPatient(){ return pref.getBoolean(loginUser, false);}
}
