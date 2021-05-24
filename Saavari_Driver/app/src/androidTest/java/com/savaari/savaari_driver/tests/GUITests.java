package com.savaari.savaari_driver.tests;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.savaari.savaari_driver.R;
import com.savaari.savaari_driver.ride.RideActivity;
import com.savaari.savaari_driver.ride.RideViewModel;
import com.savaari.savaari_driver.util.TestUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GUITests {

    @Rule
    public ActivityScenarioRule<RideActivity> activityRule
            = new ActivityScenarioRule<>(RideActivity.class);

    @Test
    public void loadUserDataTest() {
        activityRule.getScenario().onActivity(rideActivity -> {
            rideActivity.rideViewModel.driverStatus.postValue(RideViewModel.DATA_LOAD_SUCCESS);
            rideActivity.isUserDataLoaded = true;
        });

        onView(ViewMatchers.withId(R.id.go_btn))
                .check(matches(withText("SELECT VEHICLE")));

        onView(withId(R.id.bottomAppBar2))
                .check(matches(withText("You're Online")));
    }
    @Test
    public void selectVehicleTest() {
        activityRule.getScenario().onActivity(rideActivity -> {
            rideActivity.rideViewModel.setDriver(TestUtil.getInstance().loadDataForTesting());
            rideActivity.rideViewModel.driverStatus.postValue(RideViewModel.DATA_LOAD_SUCCESS);
            rideActivity.isUserDataLoaded = true;
        });

        onView(withId(R.id.go_btn))
                .perform(click());

        onView(withId(R.id.select_vehicle_card))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
    @Test
    public void markActiveTest() {
        activityRule.getScenario().onActivity(rideActivity -> {
            rideActivity.rideViewModel.setDriver(TestUtil.getInstance().loadDataForTesting());
            rideActivity.rideViewModel.driverStatus.postValue(RideViewModel.VEHICLE_SELECTED_SUCCESS);
            rideActivity.isUserDataLoaded = true;
        });

        onView(withId(R.id.go_btn))
                .check(matches(withText("ACTIVE")));

        onView(withId(R.id.go_btn))
                .perform(click());

        activityRule.getScenario().onActivity(rideActivity -> rideActivity.rideViewModel.driverStatus.postValue(RideViewModel.MATCHMAKING_STARTED_SUCCESS));

        onView(withId(R.id.bottomAppBar2))
                .check(matches(withText("Searching for Rides")));
    }
    @Test
    public void confirmRideRequestTest() {
        activityRule.getScenario().onActivity(rideActivity -> {
            rideActivity.rideViewModel.setDriver(TestUtil.getInstance().loadDataForTesting());
            rideActivity.rideViewModel.setRideRequest(TestUtil.getInstance().loadRideRequestForTesting());
            rideActivity.rideViewModel.driverStatus.postValue(RideViewModel.RIDE_REQUEST_FOUND);
            rideActivity.isUserDataLoaded = true;
        });

        onView(withId(R.id.bottomAppBar2))
                .check(matches(withText("Ride Found")));

        onView(withId(R.id.ride_detail_sub_panel))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        activityRule.getScenario().onActivity(rideActivity -> rideActivity.rideViewModel.driverStatus.postValue(RideViewModel.CONFIRM_RIDE_SUCCESS));

        onView(withId(R.id.ride_detail_sub_panel))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }
    @Test
    public void rejectRideRequestTest() {
        activityRule.getScenario().onActivity(rideActivity -> {
            rideActivity.rideViewModel.setDriver(TestUtil.getInstance().loadDataForTesting());
            rideActivity.rideViewModel.setRideRequest(TestUtil.getInstance().loadRideRequestForTesting());
            rideActivity.rideViewModel.driverStatus.postValue(RideViewModel.RIDE_REQUEST_FOUND);
            rideActivity.isUserDataLoaded = true;
        });

        onView(withId(R.id.bottomAppBar2))
                .check(matches(withText("Ride Found")));

        onView(withId(R.id.ride_detail_sub_panel))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.go_btn))
                .check(matches(withText("CANCEL")));

        activityRule.getScenario().onActivity(rideActivity -> rideActivity.rideViewModel.driverStatus.postValue(RideViewModel.CONFIRM_RIDE_FAILURE));

        onView(withId(R.id.ride_detail_sub_panel))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }
    @Test
    public void confirmNearPickupTest() {
        activityRule.getScenario().onActivity(rideActivity -> {
            rideActivity.rideViewModel.setDriver(TestUtil.getInstance().loadDataForTesting());
            rideActivity.rideViewModel.setRideRequest(TestUtil.getInstance().loadRideRequestForTesting());
            rideActivity.rideViewModel.driverStatus.postValue(RideViewModel.NEAR_PICKUP);
            rideActivity.isUserDataLoaded = true;
        });
        onView(withId(R.id.bottomAppBar2))
                .check(matches(withText("Near Pickup")));
        onView(withId(R.id.go_btn))
                .check(matches(withText("MARK ARRIVAL")));

        activityRule.getScenario().onActivity(rideActivity -> rideActivity.rideViewModel.driverStatus.postValue(RideViewModel.PICKUP_MARK_SUCCESS));

        onView(withId(R.id.bottomAppBar2))
                .check(matches(withText("Waiting for Rider")));
        onView(withId(R.id.go_btn))
                .check(matches(withText("START RIDE")));
    }
    @Test
    public void startRideTest() {
        activityRule.getScenario().onActivity(rideActivity -> {
            rideActivity.rideViewModel.setDriver(TestUtil.getInstance().loadDataForTesting());
            rideActivity.rideViewModel.setRideRequest(TestUtil.getInstance().loadRideRequestForTesting());
            rideActivity.rideViewModel.driverStatus.postValue(RideViewModel.PICKUP_MARK_SUCCESS);
            rideActivity.isUserDataLoaded = true;
        });
        onView(withId(R.id.bottomAppBar2))
                .check(matches(withText("Waiting for Rider")));
        onView(withId(R.id.go_btn))
                .check(matches(withText("START RIDE")));

        activityRule.getScenario().onActivity(rideActivity -> rideActivity.rideViewModel.driverStatus.postValue(RideViewModel.RIDE_STARTED_SUCCESS));

        onView(withId(R.id.bottomAppBar2))
                .check(matches(withText("Ride Started!")));
        onView(withId(R.id.go_btn))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }
    @Test
    public void confirmEndRideTest() {
        activityRule.getScenario().onActivity(rideActivity -> {
            rideActivity.rideViewModel.setDriver(TestUtil.getInstance().loadDataForTesting());
            rideActivity.rideViewModel.setRideRequest(TestUtil.getInstance().loadRideRequestForTesting());
            rideActivity.rideViewModel.setRide(TestUtil.getInstance().loadRideForTesting());
            rideActivity.rideViewModel.driverStatus.postValue(RideViewModel.NEAR_DEST);
            rideActivity.isUserDataLoaded = true;
        });
        onView(withId(R.id.bottomAppBar2))
                .check(matches(withText("Near Destination")));
        onView(withId(R.id.go_btn))
                .check(matches(withText(R.string.confirmRideEnd)));

        activityRule.getScenario().onActivity(rideActivity -> rideActivity.rideViewModel.driverStatus.postValue(RideViewModel.DEST_MARK_SUCCESS));

        onView(withId(R.id.bottomAppBar2))
                .check(matches(withText("You Travelled 5.0 km")));
        onView(withId(R.id.go_btn))
                .check(matches(withText("TAKE PAYMENT")));
    }
}
