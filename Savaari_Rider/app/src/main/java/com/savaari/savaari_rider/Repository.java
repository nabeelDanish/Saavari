package com.savaari.savaari_rider;

import android.util.Log;

import com.savaari.savaari_rider.services.network.NetworkUtil;
import com.savaari.savaari_rider.services.network.OnDataLoadedListener;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executor;

public class Repository {
    private static String url;
    private Executor executor;

    Repository(Executor executor) {
        this.executor = executor;
        url = loadDataSourceUrl();
        Log.d("Repository url: ", url);
    }

    public String loadDataSourceUrl() {
        Properties prop;
        String propFileName = "config.properties";
        InputStream inputStream;

        try {
            prop = new Properties();
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            // Get property value
            return prop.getProperty("dataSourceUrl");
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Sign-Up
    public void signup(OnDataLoadedListener callback, String nickname, String username, String password) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().signup(url, nickname, username, password)));
    }
    // Login
    public void login(OnDataLoadedListener callback, String username, String password) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().login(url, username, password)));
    }

    public void persistLogin(OnDataLoadedListener callback, Integer userID) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().persistLogin(url, userID)));
    }

    public void logout(OnDataLoadedListener callback, Integer userID) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().logout(url, userID)));
    }
    // Loading User Data
    public void loadUserData(OnDataLoadedListener callback, int currentUserID) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().loadUserData(url, currentUserID)));
    }
}
