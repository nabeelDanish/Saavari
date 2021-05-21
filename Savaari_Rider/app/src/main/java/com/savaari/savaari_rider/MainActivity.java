package com.savaari.savaari_rider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.savaari.savaari_rider.auth.login.LoginActivity;
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

        // Expand logo animation
        ImageView logo = findViewById(R.id.logo);
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom);
        logo.startAnimation(animation);

        SharedPreferences sh
                = getSharedPreferences("AuthSharedPref",
                MODE_PRIVATE);

        final int USER_ID = sh.getInt("USER_ID", -1);
        if (USER_ID == -1) {
            launchLoginActivity();
        }
        else {
            // Launch Ride Activity
        }
    }

    public void launchLoginActivity() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}