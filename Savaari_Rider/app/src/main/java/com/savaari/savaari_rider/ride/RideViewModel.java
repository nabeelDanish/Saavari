package com.savaari.savaari_rider.ride;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.savaari.savaari_rider.Repository;
import com.savaari.savaari_rider.ride.entity.Location;
import com.savaari.savaari_rider.ride.entity.Ride;
import com.savaari.savaari_rider.ride.entity.RideRequest;
import com.savaari.savaari_rider.ride.entity.Rider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class RideViewModel extends ViewModel {

    private static String LOG_TAG = RideViewModel.class.getSimpleName();
    private final Repository repository;

    /* Credentials for netowrk operations */
    private int USER_ID = -1;

    /* User account data*/
    private LatLng userCoordinates;

    private Ride previousRide;
    private Ride ride;

    /* User locations data for pinging */
    private ArrayList<Location> mUserLocations = new ArrayList<>();
}
