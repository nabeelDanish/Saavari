package com.savaari.savaari_rider.ride.log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import static java.security.AccessController.getContext;

public class RideLog extends Util implements LogViewClickListener {

    private int USER_ID = -1;
    private RideLogViewModel rideLogViewModel;
    private Toolbar myToolbar;
    private LinearLayout reportProblemPanel;
    private Button submitRatingButton;
    private EditText problemDescriptionText;

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
            problemDescriptionText = findViewById(R.id.problem_desc);
            reportProblemPanel = findViewById(R.id.report_problem_panel);
            submitRatingButton = findViewById(R.id.submit_rating);

            rideLogViewModel = new ViewModelProvider(this, new RideLogViewModelFactory(USER_ID,
                    ((SavaariApplication) this.getApplication()).getRepository())).get(RideLogViewModel.class);

            rideLogViewModel.fetchRideLog();
            rideLogViewModel.isRideLogFetched().observe(this, this::onRideLogReady);
        }
    }

    private void toggleReportProblemPanel(boolean visibility, boolean withAnimation) {
        if (visibility && reportProblemPanel.getVisibility() != View.VISIBLE) {
            if (withAnimation) {
                reportProblemPanel.setAnimation(inFromBottomAnimation(400));
            }
            reportProblemPanel.setVisibility(View.VISIBLE);
        }
        else if (!visibility && reportProblemPanel.getVisibility() != View.GONE) {
            if (withAnimation) {
                reportProblemPanel.setAnimation(outToBottomAnimation(400));
            }
            reportProblemPanel.setVisibility(View.GONE);
        }
    }

    private void onRideLogReady(ArrayList<Ride> rideLog) {
        RideLogAdapter rideTypeAdapter = new RideLogAdapter(rideLog, this);
        RecyclerView rideLogRecyclerView = findViewById(R.id.ride_log);
        rideLogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rideLogRecyclerView.setAdapter(rideTypeAdapter);
    }

    private void onProblemReported(Boolean problemReported) {
        Toast.makeText(this, "Problem Reported", Toast.LENGTH_LONG).show();
    }

    private void submitReport(String problemDescription, int position) {
        rideLogViewModel.reportProblem(problemDescription, position).observe(this, this::onProblemReported);
    }

    @Override
    public void onRideItemClick(int position) {
        // TODO: Go to provide feedback screen

        toggleReportProblemPanel(true, true);
        submitRatingButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            submitReport(problemDescriptionText.getText().toString(), position);
            toggleReportProblemPanel(false, true);
        });

        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.report_problem);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submitFeedback(input.getText().toString(), position);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();*/
    }
}