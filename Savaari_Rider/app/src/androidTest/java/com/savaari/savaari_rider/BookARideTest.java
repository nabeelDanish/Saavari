package com.savaari.savaari_rider;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.gms.maps.model.LatLng;
import com.savaari.savaari_rider.ride.RideActivity;
import com.savaari.savaari_rider.ride.entity.Driver;
import com.savaari.savaari_rider.ride.entity.Location;
import com.savaari.savaari_rider.ride.entity.Ride;
import com.savaari.savaari_rider.ride.entity.RideRequest;
import com.savaari.savaari_rider.ride.entity.Rider;
import com.savaari.savaari_rider.ride.entity.Vehicle;
import com.savaari.savaari_rider.utility.BaseRobot;
import com.savaari.savaari_rider.utility.CustomViewAction;

import java.util.ArrayList;
import java.util.Collection;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BookARideTest {

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), RideActivity.class);
        intent.putExtra("API_CONNECTION", true);
        intent.putExtra("USER_ID", 1);
    }

    //@Rule
    //public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Rule
    public ActivityScenarioRule<RideActivity> activityRule
            = new ActivityScenarioRule<>(intent);

    @Test
    public void selectRideTypeTest() {
        // Perform click on Ride Type config button


        BaseRobot baseRobot = new BaseRobot();
        baseRobot.doOnView(withId(R.id.ride_type_config), new CustomViewAction());

        // Check if panel header changed
        onView(withId(R.id.ride_type_header))
                .check(matches(withText(R.string.select_ride_type)));

        // Check if Ride Type Panel is visible
        onView(withId(R.id.ride_type_panel))
                .check(matches(isDisplayed()));

        for (int position = 0 ; position < 4 ; position++) {
            baseRobot.doOnView(allOf(isDisplayed(), withId(R.id.select_ride_type)),
                    RecyclerViewActions.actionOnItemAtPosition(position, new CustomViewAction()));

            int finalPosition = position;
            activityRule.getScenario().onActivity(it -> {
                RideActivity rideActivity = (RideActivity) it;

                Assert.assertEquals(finalPosition + 1, rideActivity.rideViewModel.getRide().getRideParameters().getRideType().getTypeID());
            });

            baseRobot = new BaseRobot();
            baseRobot.doOnView(allOf(isDisplayed(), withId(R.id.ride_type_config)), new CustomViewAction());
        }

    }

    @Test
    public void selectPaymentMethodTest() {
        // Perform click on Ride Type config button


        BaseRobot baseRobot = new BaseRobot();
        baseRobot.doOnView(withId(R.id.payment_config), new CustomViewAction());

        // Check if Ride Type Panel is visible
        onView(withId(R.id.payment_method_panel))
                .check(matches(isDisplayed()));

        // Test all possible Payment Methods
        for (int position = 0 ; position < 2 ; position++) {
            baseRobot.doOnView(allOf(isDisplayed(), isEnabled(), withId(R.id.select_payment_method)),
                    RecyclerViewActions.actionOnItemAtPosition(position, new CustomViewAction()));

            int finalPosition = position;
            activityRule.getScenario().onActivity(it -> {
                RideActivity rideActivity = (RideActivity) it;
                Assert.assertEquals(finalPosition + 1, rideActivity.rideViewModel.getRide().getRideParameters().getPaymentMethod());
            });

            baseRobot = new BaseRobot();
            baseRobot.doOnView(allOf(isDisplayed(), withId(R.id.payment_config)), new CustomViewAction());
        }

    }
    @Test
    public void onSearchRideActionTest() {
        // Test Search Ride UI functionality with & without network
        onSearchRideActionWithNetworkConfigTest(false);
        onSearchRideActionWithNetworkConfigTest(true);
    }
    
    private Rider setFakeRider(Rider rider) {
        rider.setUsername("samplerider");
        rider.setCurrentLocation(new Location(25.351364, 55.397967));
        rider.setEmailAddress("samplerider@gmail.com");
        rider.setPassword("password2111P");
        rider.setPhoneNo("03462135667");
        rider.setFirstName("Sample");
        rider.setLastName("Rider");
        rider.setRating(4.3f);
        return rider;
    }

    private Driver setFakeDriver(Driver driver) {
        driver.setUsername("sampledriver");
        driver.setCurrentLocation(new Location(25.351364, 55.397967));
        driver.setEmailAddress("sampledriver@gmail.com");
        driver.setPassword("password2111P");
        driver.setPhoneNo("03462135667");
        driver.setFirstName("Sample");
        driver.setLastName("Driver");
        driver.setRating(4.5f);

        Vehicle v = new Vehicle();
        v.setColor("Black");
        v.setMake("Honda");
        v.setModel("City");
        v.setYear("2013");
        v.setRideTypeID(3);
        v.setVehicleTypeID(5);
        v.setNumberPlate("AES-216");
        driver.setActiveVehicle(v);
        return driver;
    }
    
    private Ride fetchFakeRide() {
        Ride ride = new Ride();
        RideRequest rideParameters = ride.getRideParameters();
        Rider rider = rideParameters.getRider();
        Driver driver = rideParameters.getDriver();

        rideParameters.setDriver(setFakeDriver(driver));
        rideParameters.setRider(setFakeRider(rider));
        rideParameters.setPickupLocation(new LatLng(25.351364, 55.397967), "random pickup");
        rideParameters.setDropoffLocation(new LatLng(25.197197,  55.274376), "random dropoff");
        rideParameters.setFindStatus(RideRequest.PAIRED);

        return ride;
    }

    @Test
    public void onSearchRideActionTest() {
        // Test Search Ride UI functionality with & without network
        onSearchRideActionWithNetworkConfigTest(false);
        onSearchRideActionWithNetworkConfigTest(true);
    }
    public void onSearchRideActionWithNetworkConfigTest(boolean networkAvailable) {
        LatLng pickupLocation = new LatLng(25.351364, 55.397967);
        LatLng dropOffLocation = new LatLng(25.197197,  55.274376);

        BaseRobot baseRobot = new BaseRobot();

        // Network Config
        activityRule.getScenario().onActivity(rideActivity -> {
            // Disable WiFi to prevent Matchmaking
            WifiManager wifi=(WifiManager) rideActivity.getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(networkAvailable);
        });

        /* Without Pickup & Drop-off Location - expect Failure */

        // Set state
        activityRule.getScenario().onActivity(rideActivity -> {
                rideActivity.rideViewModel.getRide().getRideParameters().setPickupLocation(null, "");
                rideActivity.rideViewModel.getRide().getRideParameters().setDropoffLocation(null, "");
            });

        // Search for Ride
        baseRobot.doOnView(allOf(isDisplayed(), isEnabled(), withId(R.id.go_btn)), new CustomViewAction());

        // Assertions
        activityRule.getScenario().onActivity(rideActivity -> {
            // Should remain null
            assertNull(rideActivity.rideViewModel.getRide().getRideParameters().getPickupLocation());
            assertNull(rideActivity.rideViewModel.getRide().getRideParameters().getDropoffLocation());
        });

        onView(withId(R.id.ride_select_panel)).check(matches(isDisplayed()));
        onView(withId(R.id.search_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.go_btn)).check(matches(isEnabled()));
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));


        /* Without Drop-off Location - expect Failure */

        // Set state
        activityRule.getScenario().onActivity(rideActivity -> {
            rideActivity.rideViewModel.getRide().getRideParameters().setPickupLocation(pickupLocation, "");
            rideActivity.rideViewModel.getRide().getRideParameters().setDropoffLocation(null, "");
        });

        // Search for Ride
        baseRobot.doOnView(allOf(isDisplayed(), isEnabled(), withId(R.id.go_btn)), new CustomViewAction());

        // Assertions
        activityRule.getScenario().onActivity(rideActivity -> {
            // Should remain the same
            assertNotNull(rideActivity.rideViewModel.getRide().getRideParameters().getPickupLocation());
            assertNull(rideActivity.rideViewModel.getRide().getRideParameters().getDropoffLocation());
        });

        // Views should remain with the same Visibility
        onView(withId(R.id.ride_select_panel)).check(matches(isDisplayed()));
        onView(withId(R.id.search_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.go_btn)).check(matches(isEnabled()));
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));


        /* Without Pickup Location - use current location as pickup */

        // Set state
        activityRule.getScenario().onActivity(rideActivity -> {
            rideActivity.rideViewModel.getRide().getRideParameters().setPickupLocation(null, "");
            rideActivity.rideViewModel.getRide().getRideParameters().setDropoffLocation(dropOffLocation, "");
        });

        // Search for Ride
        baseRobot.doOnView(allOf(isDisplayed(), isEnabled(), withId(R.id.go_btn)), new CustomViewAction());

        // Assertions
        activityRule.getScenario().onActivity(rideActivity -> {

            // Pickup location should be inferred from device location, making both not null
            assertNotNull(rideActivity.rideViewModel.getRide().getRideParameters().getPickupLocation());
            assertNotNull(rideActivity.rideViewModel.getRide().getRideParameters().getDropoffLocation());
        });


        /* With Pickup & Dropoff - use current location as pickup */

        // Set state
        activityRule.getScenario().onActivity(rideActivity -> {
            rideActivity.rideViewModel.getRide().getRideParameters().setPickupLocation(pickupLocation, "");
            rideActivity.rideViewModel.getRide().getRideParameters().setDropoffLocation(dropOffLocation, "");
        });

        // Search for Ride
        baseRobot.doOnView(allOf(isDisplayed(), isEnabled(), withId(R.id.go_btn)), new CustomViewAction());

        // Assertions
        activityRule.getScenario().onActivity(rideActivity -> {
            // Should remain not null
            assertNotNull(rideActivity.rideViewModel.getRide().getRideParameters().getPickupLocation());
            assertNotNull(rideActivity.rideViewModel.getRide().getRideParameters().getDropoffLocation());
        });

        // Find Driver will fail, can't test with Database, wait for state to reset
        baseRobot.waitForView(withId(R.id.go_btn), 6000, 500);

        // Then test manually by manually simulating state
        // TODO: Mock database?
        activityRule.getScenario().onActivity(rideActivity -> {
                    if (networkAvailable) {
                        rideActivity.toggleRideSearchBar(false, true);
                        Ride ride = fetchFakeRide();
                        rideActivity.rideViewModel.setRide(ride);
                        rideActivity.rideFoundAction(ride);
                    }
        });

        // Test ride found case
        if (networkAvailable) {
            // TODO: Test by observing LiveData, Views visibility should stay the same as before search ride
            onView(withId(R.id.ride_select_panel)).check(matches(not(isDisplayed())));
            onView(withId(R.id.search_bar)).check(matches(not(isDisplayed())));
            onView(withId(R.id.go_btn)).check(matches(not(isEnabled())));
        }
    }
}