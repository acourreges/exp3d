package com.breakingbyte.wrap;

import android.content.SharedPreferences;

public class Persistence {
    
    private static final String PREFS_NAME = "PREF";
    private static final String PREFS_STR = "PREF_STR";
    
    private static SharedPreferences settings;
    
    public static final void init() {
        settings = BaseActivity.instance.getSharedPreferences(PREFS_NAME, 0);
    }
    
    public static final String readPreferences() {
        return settings.getString(PREFS_STR, "");
    }
    
    public static final void writePreferences(String str) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREFS_STR, str);
        editor.commit();
    }

}
