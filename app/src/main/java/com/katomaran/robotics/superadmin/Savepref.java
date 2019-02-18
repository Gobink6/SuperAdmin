package com.katomaran.robotics.superadmin;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Savepref extends Activity {
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";

    public static final String Host = "Host";


    public void saveString(Context context, String key, String String) {
        SharedPreferences sharedPref = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, String);
        editor.commit();
    }

    public String getString(Context context, String key) {
        SharedPreferences settings;
        String text;
        settings = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        text = settings.getString(key, "");
        return text;
    }

    public void saveInt(Context context, String key, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(Context context, String key) {
        SharedPreferences settings;
        int integer;
        settings = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        integer = settings.getInt(key, 0);
        return integer;
    }

    public void clearSharedPreference(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public void removeValue(Context context, String key) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.remove(key);
        editor.commit();
    }

}
