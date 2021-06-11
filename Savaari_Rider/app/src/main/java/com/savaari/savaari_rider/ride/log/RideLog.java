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

import com.savaari.savaari_rider.MainActivity;
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
    private Button cancelRatingButton;
    private EditText problemDescriptionText;
    private Button categorySelectButton;

    private AlertDialog.Builder alertDialog;

    private boolean categoryPickerDisplayed = false;
    private boolean reportPanelDisplayed = false;

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
            categorySelectButton = findViewById(R.id.cat_select_btn);
            problemDescriptionText = findViewById(R.id.problem_desc);
            reportProblemPanel = findViewById(R.id.report_problem_panel);
            submitRatingButton = findViewById(R.id.submit_rating);
            cancelRatingButton = findViewById(R.id.cancel_rating);

            categorySelectButton.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                showCategoryPicker();
            });

            cancelRatingButton.setOnClickListener(v -> {
                toggleReportProblemPanel(false, true);
                rideLogViewModel.setCategorySelected(-1);
            });

            rideLogViewModel = new ViewModelProvider(this, new RideLogViewModelFactory(USER_ID,
                    ((SavaariApplication) this.getApplication()).getRepository())).get(RideLogViewModel.class);

            rideLogViewModel.fetchRideLog();
            rideLogViewModel.isRideLogFetched().observe(this, this::onRideLogReady);
        }
    }

    private void showCategoryPicker() {
        alertDialog = new AlertDialog.Builder(RideLog.this);
        alertDialog.setTitle("Select a Problem Category");
        String[] items = {"Pickup / Drop-off", "Route", "Fare", "Driver Conduct", "Other"};
        int checkedItem = (rideLogViewModel.getCategorySelected() == -1)? 0 : rideLogViewModel.getCategorySelected();
        rideLogViewModel.setCategorySelected(0);
        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rideLogViewModel.setCategorySelected(which);
            }
        });

        alertDialog.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                categorySelectButton.setText(items[rideLogViewModel.getCategorySelected()]);
                categoryPickerDisplayed = false;
            }
        });

        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                rideLogViewModel.setCategorySelected(-1);
                categoryPickerDisplayed = false;
            }
        });

        categoryPickerDisplayed = true;
        alertDialog.show();
    }

    private void toggleReportProblemPanel(boolean visibility, boolean withAnimation) {
        if (visibility && reportProblemPanel.getVisibility() != View.VISIBLE) {
            if (withAnimation) {
                reportProblemPanel.setAnimation(inFromBottomAnimation(400));
            }
            reportProblemPanel.setVisibility(View.VISIBLE);
            reportPanelDisplayed = true;
        }
        else if (!visibility && reportProblemPanel.getVisibility() != View.GONE) {
            if (withAnimation) {
                reportProblemPanel.setAnimation(outToBottomAnimation(400));
            }
            reportProblemPanel.setVisibility(View.GONE);
            reportPanelDisplayed = false;
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
            String problemDescription = problemDescriptionText.getText().toString();
            if (rideLogViewModel.getCategorySelected() == -1) {
                Toast.makeText(this, "Please select a problem category", Toast.LENGTH_SHORT).show();
            }
            else if (problemDescription.length() < 50) {
                Toast.makeText(this, "Description is too short (" +
                        (50-problemDescription.length()) + " characters below min", Toast.LENGTH_SHORT).show();
            }
            else if (problemDescription.length() > 500) {
                Toast.makeText(this, "Description is too long (" +
                        (problemDescription.length() - 500) + " characters above max", Toast.LENGTH_SHORT).show();
            }
            else {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                submitReport(problemDescription, position);
                toggleReportProblemPanel(false, true);
                rideLogViewModel.setCategorySelected(-1);
            }
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

    @Override
    public boolean onSupportNavigateUp() {
        if (categoryPickerDisplayed) {
            alertDialog.create().dismiss();
            categoryPickerDisplayed = false;
        }
        else if (reportPanelDisplayed) {
            toggleReportProblemPanel(false, true);
        }
        else {
            onBackPressed();
            return true;
        }
        return false;
    }

}