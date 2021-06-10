package com.savaari.savaari_rider.ride.log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.savaari.savaari_rider.R;
import com.savaari.savaari_rider.SavaariApplication;
import com.savaari.savaari_rider.ride.RideViewModel;
import com.savaari.savaari_rider.ride.RideViewModelFactory;
import com.savaari.savaari_rider.ride.adapter.LogViewClickListener;
import com.savaari.savaari_rider.ride.adapter.RideLogAdapter;
import com.savaari.savaari_rider.ride.entity.Ride;
import com.savaari.savaari_rider.utility.ThemeVar;
import com.savaari.savaari_rider.utility.Util;

import java.util.ArrayList;

public class RideLog extends Util implements LogViewClickListener {

    private int USER_ID = -1;
    private RideLogViewModel rideLogViewModel;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeVar.getInstance().themeSelect(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_log);

        myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle("Your Trips");
        setSupportActionBar(myToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Intent recvIntent = getIntent();
        USER_ID = recvIntent.getIntExtra("USER_ID", -1);

        if (USER_ID == -1) {
            SharedPreferences sh
                    = getSharedPreferences("AuthSharedPref",
                    MODE_PRIVATE);

            USER_ID = sh.getInt("USER_ID", -1);

            Toast.makeText(RideLog.this, "There was a problem fetching your Ride Log", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
        else {
            rideLogViewModel = new ViewModelProvider(this, new RideLogViewModelFactory(USER_ID,
                    ((SavaariApplication) this.getApplication()).getRepository())).get(RideLogViewModel.class);

            rideLogViewModel.fetchRideLog();
            rideLogViewModel.isRideLogFetched().observe(this, this::onRideLogReady);
        }
    }

    private void onRideLogReady(ArrayList<Ride> rideLog) {
        RideLogAdapter rideTypeAdapter = new RideLogAdapter(rideLog, this);
        RecyclerView rideLogRecyclerView = findViewById(R.id.ride_log);
        rideLogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rideLogRecyclerView.setAdapter(rideTypeAdapter);
    }

    @Override
    public void onRideItemClick(int position) {
        // TODO: Go to provide feedback screen
    }
}