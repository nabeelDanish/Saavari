package com.savaari.savaari_driver.auth.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.savaari.savaari_driver.R;
import com.savaari.savaari_driver.Repository;
import com.savaari.savaari_driver.auth.AuthInputValidator;
import com.savaari.savaari_driver.entity.Driver;

public class LoginViewModel extends ViewModel {

    // Main Attributes
    private final String LOG_TAG = this.getClass().getCanonicalName();
    private final MutableLiveData<Integer> userID = new MutableLiveData<>();
    private MutableLiveData<com.savaari.savaari_driver.auth.login.LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<RecoveryFormState> recoveryFormState = new MutableLiveData<>();

    private Repository repository;
    private Driver driver;

    private final MutableLiveData<Boolean> userdataLoaded = new MutableLiveData<>();

    // Methods
    LiveData<com.savaari.savaari_driver.auth.login.LoginFormState> getLoginFormState() {
        return loginFormState;
    }
    LiveData<RecoveryFormState> getRecoveryFormState() {
        return recoveryFormState;
    }

    public LiveData<Integer> getUserID() {
        return userID;
    }

    public LoginViewModel(Repository repository)
    {
        this.repository = repository;
        driver = repository.getDriver();
    }

    public void loginAction(String username, String password) {
        repository.login(object -> {
            Integer ID;
            try {
                ID = (Integer) object;
                userID.postValue(ID);
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "loginAction(): exception");
            }

        }, username, password);
    }
    // Function to load Driver data and store in repository
    public void loadDriverData(int userID) {
        repository.loadUserData(object -> {
            try {
                if (object != null) {
                    repository.setDriver((Driver) object);
                    driver = repository.getDriver();
                    Log.d(LOG_TAG, "loadDriverData: User Data Loaded!");
                    userdataLoaded.postValue(true);
                } else {
                    userdataLoaded.postValue(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                userdataLoaded.postValue(false);
            }
        }, userID);
    }

    public void recoveryEmailDataChanged(String username) {
        if (!AuthInputValidator.isUserNameValid(username))
            recoveryFormState.setValue(new RecoveryFormState(R.string.invalid_username));
        else
            recoveryFormState.setValue(new RecoveryFormState(true));
    }

    public void loginDataChanged(String username, String password) {

        boolean isValid = true, userNameValid = AuthInputValidator.isUserNameValid(username);
        int passwordValidityType = AuthInputValidator.isPasswordValid(password);

        if (!userNameValid) {
            isValid = false;
            loginFormState.setValue(new com.savaari.savaari_driver.auth.login.LoginFormState(R.string.invalid_username, null));
        }
        if (passwordValidityType != 0) {
            isValid = false;

            switch (passwordValidityType)
            {
                case (1):
                    loginFormState.setValue(new com.savaari.savaari_driver.auth.login.LoginFormState(null, R.string.invalid_password1));
                    break;
                case (2):
                    loginFormState.setValue(new com.savaari.savaari_driver.auth.login.LoginFormState(null, R.string.invalid_password2));
                    break;
                case (3):
                    loginFormState.setValue(new com.savaari.savaari_driver.auth.login.LoginFormState(null, R.string.invalid_password3));
                    break;
                case (4):
                    loginFormState.setValue(new com.savaari.savaari_driver.auth.login.LoginFormState(null, R.string.invalid_password4));
                    break;
                case (5):
                    loginFormState.setValue(new com.savaari.savaari_driver.auth.login.LoginFormState(null, R.string.invalid_password5));
                    break;
                case (6):
                    loginFormState.setValue(new com.savaari.savaari_driver.auth.login.LoginFormState(null, R.string.invalid_password6));
                    break;
                case (7):
                    loginFormState.setValue(new com.savaari.savaari_driver.auth.login.LoginFormState(null, R.string.invalid_password7));
                    break;
            }
        }

       if (isValid) {

           loginFormState.setValue(new com.savaari.savaari_driver.auth.login.LoginFormState(true));
       }
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        Log.d("this happened!", "the fuck");
    }

    // Getters and Setters
    public MutableLiveData<Boolean> getUserdataLoaded() {
        return userdataLoaded;
    }
    public Driver getDriver() {
        return driver;
    }
    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}