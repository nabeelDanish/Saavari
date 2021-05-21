package com.savaari.savaari_rider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.savaari.savaari_rider.utility.ThemeVar;
import com.savaari.savaari_rider.utility.Util;


public class MainActivity extends Util {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        ThemeVar.getInstance().setData(preferences.getInt(getString(R.string.preference_theme_var), ThemeVar.getInstance().getData()));

        switch (ThemeVar.getInstance().getData()) {
            case (0):
                setTheme(R.style.BlackTheme);
                break;
            case (1):
                setTheme(R.style.RedTheme);
                break;
            default:
                setTheme(R.style.BlueTheme);
                break;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}