package com.savaari.savaari_driver.services.location;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class LocationUpdateService extends Service {
    // Main Attributes
    private static final String LOG_TAG = "LocationUpdateService";
    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 10 * 1000; // 10 seconds
    private final static long FASTEST_INTERVAL = 10 * 1000; // 2 seconds
    private int mUserID;

    // Location Callback Function
    private final LocationCallback locationCallback = new LocationCallback()
    {
        @Override
        public void onLocationResult(LocationResult locationResult)
        {
            Log.v(LOG_TAG, "onLocationResult: got Location result");
            Location location = locationResult.getLastLocation();

            // Saving the Location Data on database
            // Call Save User
            try
            {
                LocationUpdateUtil.saveUserLocation(new LatLng(location.getLatitude(), location.getLongitude()), LocationUpdateService.this);
                Intent intent = new Intent("Update");
                intent.putExtra("Location", location);
                LocalBroadcastManager.getInstance(LocationUpdateService.this).sendBroadcast(intent);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    public LocationUpdateService()
    {
        // Empty Constructor
    }

    public LocationUpdateService(int mUserID)
    {
        this.mUserID = mUserID;
    }

    // Method that needs to be Implemented because of extending Service
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Main onCreate Override
    @Override
    public void onCreate() {

        super.onCreate();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= 26)
        {
            // Creating the Notification Channel for Location Service
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            // Setting First Notification
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            // Starting Service in foreground of android
            startForeground(1, notification);
        }
    }

    // Overriding the onStartCommand for this Service so that it uses Location Code
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(LOG_TAG, "onStartCommand: called.");
        getLocation();
        return START_NOT_STICKY;
    }

    // Member function for setting location data
    private void getLocation()
    {
        Log.v(LOG_TAG, "getLocation: Inside function!");

        // Quit the looper if the user is signed out
        if (mUserID == -1)
        {
            Looper.myLooper().quitSafely();
        }

        // Creating the Location Request
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);

        // Check if we have Location Permissions from Google
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Log.v(LOG_TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }

        // Getting the Location Information and calling the SaveUserLocation Function
        Log.v(LOG_TAG, "getLocation: getting Location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy,
                locationCallback, Looper.myLooper()); // Added a Looper at the End to repeat the function
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy: Service Destroyed!");
        mFusedLocationClient.removeLocationUpdates(locationCallback);
        stopSelf();
    }
}
