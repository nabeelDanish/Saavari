package com.savaari.savaari_driver.auth.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import com.savaari.savaari_driver.R;
import com.savaari.savaari_driver.SavaariApplication;
import com.savaari.savaari_driver.ThemeVar;
import com.savaari.savaari_driver.Util;
import com.savaari.savaari_driver.auth.signup.SignUpActivity;
import com.savaari.savaari_driver.entity.Driver;
import com.savaari.savaari_driver.entity.Vehicle;
import com.savaari.savaari_driver.register.RegisterActivity;
// import com.savaari.savaari_driver.ride.RideActivity;

public class LoginActivity extends Util
{
    // Main Attributes
    private LoginViewModel loginViewModel;      // input validation
    private EditText usernameEditText, passwordEditText, recoveryEmailEditText;
    private Button loginButton, newAccountButton, backFromBanner, forgotPasswordButton;
    private ImageButton closeBanner;
    private ConstraintLayout recoveryEmailBanner, forgotPasswordBanner, emailSentBanner;
    private ProgressBar loadingProgressBar, recoveryProgressBar;
    boolean isEmailSent = false;                // forgot password transition management


    @Override
    public void onCreate(Bundle savedInstanceState) {

        ThemeVar.getInstance().themeSelect(this);

        // Default Codes
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Initialize members & register receiver */
        init();

        forgotPasswordBannerHandler();
        loginFormStateWatcher();
        recoveryFormStateWatcher();
        loginRequestHandler();

        // Launches Sign up Activity
        newAccountButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            Intent i = new Intent(com.savaari.savaari_driver.auth.login.LoginActivity.this, SignUpActivity.class);
            startActivity(i);
        });
    }

    private void init() {
        loginViewModel = ViewModelProviders.of(this, new com.savaari.savaari_driver.auth.login.LoginViewModelFactory(
                ((SavaariApplication) this.getApplication()).getRepository())
        ).get(LoginViewModel.class);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        recoveryEmailEditText = findViewById(R.id.recoveryEmail);
        loginButton = findViewById(R.id.login);
        newAccountButton = findViewById(R.id.newAccountBTN);

        closeBanner = findViewById(R.id.closeBanner);

        backFromBanner = findViewById(R.id.backFromBanner);
        forgotPasswordBanner = findViewById(R.id.forgotPasswordBanner);
        emailSentBanner = findViewById(R.id.emailSentPanel);

        forgotPasswordButton = findViewById(R.id.forgotPassword);
        loadingProgressBar = findViewById(R.id.loading);
        recoveryEmailBanner = findViewById(R.id.recoveryEmailBanner);
        forgotPasswordBanner = findViewById(R.id.forgotPasswordBanner);
        recoveryProgressBar = findViewById(R.id.recoveryProgressBar);


        // Visibility settings for forgot password banner
        forgotPasswordBanner.setVisibility(View.INVISIBLE);
        emailSentBanner.setVisibility(View.INVISIBLE);
        backFromBanner.setText(R.string.pass_reset_btn_text);
    }

    private void forgotPasswordBannerHandler() {
        // Displays forgot password banner
        forgotPasswordButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            emailSentBanner.setVisibility(View.INVISIBLE);
            recoveryEmailBanner.setVisibility(View.VISIBLE);
            backFromBanner.setText(R.string.pass_reset_btn_text);

            forgotPasswordBanner.startAnimation(inFromBottomAnimation(250));
            forgotPasswordBanner.setVisibility(View.VISIBLE);
        });

        // Handles forgot-pass banner interactions
        closeBanner.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            forgotPasswordBanner.startAnimation(outToBottomAnimation());
            forgotPasswordBanner.setVisibility(View.INVISIBLE);
            emailSentBanner.startAnimation(outToRightAnimation(500));
            isEmailSent = false;
        });


        // [ sends recovery email on first button press ] + [ retracts banner on second press]
        backFromBanner.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            if (isEmailSent)
            {
                forgotPasswordBanner.startAnimation(outToBottomAnimation());
                forgotPasswordBanner.setVisibility(View.INVISIBLE);
                emailSentBanner.startAnimation(outToRightAnimation(500));
                isEmailSent = false;
            }
            else
            {
                recoveryProgressBar.setVisibility(View.VISIBLE);

                //TODO: Handle Password Reset Action
            }
        });
    }


    private void loginFormStateWatcher() {
        // Receives and displays input validation messages - for login page

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        // Listener for login page input fields

        TextWatcher afterLoginTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };

        usernameEditText.addTextChangedListener(afterLoginTextChangedListener);
        passwordEditText.addTextChangedListener(afterLoginTextChangedListener);
    }


    private void recoveryFormStateWatcher() {
        // Receives and displays input validation messages - for password recovery banner
        loginViewModel.getRecoveryFormState().observe(this, recoveryFormState -> {

            if (recoveryFormState == null)
                return;

            backFromBanner.setEnabled(recoveryFormState.isDataValid());

            if (recoveryFormState.getRecoveryEmailError() != null) {
                recoveryEmailEditText.setError(getString(recoveryFormState.getRecoveryEmailError()));
            }
        });

        // Listener for recovery page input fields
        TextWatcher afterRecoveryTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.recoveryEmailDataChanged(recoveryEmailEditText.getText().toString());
            }
        };

        recoveryEmailEditText.addTextChangedListener(afterRecoveryTextChangedListener);
    }


    private void loginRequestHandler() {
        // Sends login requests to loginAction()
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                loginAction(loadingProgressBar, usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {

            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

            View view = getCurrentFocus();
            if (view == null)
                view = new View(getApplicationContext());

            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            loginAction(loadingProgressBar, usernameEditText.getText().toString(), passwordEditText.getText().toString());
            // Setting Up Response Action
            loginViewModel.getUserID().observe(com.savaari.savaari_driver.auth.login.LoginActivity.this, this::loginResponseAction);
        });
    }

    private void loginResponseAction(Integer integer) {
        int USER_ID = integer;
        loadingProgressBar.setVisibility(View.GONE);

        SharedPreferences sharedPreferences
                = getSharedPreferences("AuthSharedPref", MODE_PRIVATE);

        SharedPreferences.Editor myEdit
                = sharedPreferences.edit();

        if (USER_ID <= 0) {
            Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
            myEdit.putInt("USER_ID", -1);
            myEdit.apply();
        }
        else {
            Toast.makeText(getApplicationContext(), "Logged in successfully", Toast.LENGTH_LONG).show();

            myEdit.putInt("USER_ID", USER_ID);
            myEdit.commit();

            // Loading User Data and applying checks
            loginViewModel.loadDriverData(USER_ID);
            loginViewModel.getUserdataLoaded().observe(this, aBoolean ->
            {
                if (aBoolean != null)
                {
                    if (aBoolean)
                    {
                        // Check Driver's eligibility to move to RideActivity
                        Intent i = null;
                        Driver driver = loginViewModel.getDriver();
                        if (driver.getStatus() == Driver.DV_REQ_APPROVED && driver.getActiveVehicle().getVehicleID() == Vehicle.VH_REQ_ACCEPTED)
                        {
                            // i = new Intent(com.savaari.savaari_driver.auth.login.LoginActivity.this, RideActivity.class);
                            Toast.makeText(getApplicationContext(), "Logged In! Can Ride!", Toast.LENGTH_LONG).show();
                        } else {
                            i = new Intent(com.savaari.savaari_driver.auth.login.LoginActivity.this, RegisterActivity.class);
                            Toast.makeText(getApplicationContext(), "Logged In! Need to Register!", Toast.LENGTH_LONG).show();
                        }
                        if (i != null) {
                            startActivity(i);
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "User Data could not be loaded!", Toast.LENGTH_LONG).show();
                    }
                }
            });// End of User Data Loaded Listener
        } // End of Login Successful
    }// End of LoginResponseAction

    // Method: Handles Login Request
    private void loginAction(final ProgressBar loadingProgressBar, final String username, final String password) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        loginViewModel.loginAction(username, password);
    }

    @Override
    public void onBackPressed() {
        if (isEmailSent) {
            forgotPasswordBanner.startAnimation(outToBottomAnimation());
            forgotPasswordBanner.setVisibility(View.INVISIBLE);
            emailSentBanner.startAnimation(outToRightAnimation(500));
            isEmailSent = false;
        }
        else {
            super.onBackPressed();
        }
    }
}