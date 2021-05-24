package com.savaari.savaari_rider;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.savaari.savaari_rider.ride.RideActivity;
import com.savaari.savaari_rider.ride.entity.Ride;
import com.savaari.savaari_rider.utility.BaseRobot;
import com.savaari.savaari_rider.utility.CustomViewAction;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GiveFeedbackTest {
    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), RideActivity.class);
        intent.putExtra("API_CONNECTION", true);
        intent.putExtra("USER_ID", 1);
    }

    @Rule
    public ActivityScenarioRule<RideActivity> activityRule
            = new ActivityScenarioRule<>(intent);

    @Test
    public void toggleEndOfRideDetailsPanelTest() {
        BaseRobot baseRobot = new BaseRobot();

        // Test for Arrived at destination, but end of ride not acknowledged case

        activityRule.getScenario().onActivity(rideActivity -> {
            rideActivity.rideViewModel.getRide().setRideStatus(Ride.ARRIVED_AT_DEST);
            rideActivity.toggleEndOfRideDetailsPanel();
        });

        baseRobot.waitForView(withId(R.id.end_of_ride_details_panel), 4000, 500);
        onView(withId(R.id.submit_rating)).check(matches(not(isDisplayed())));
        onView(withId(R.id.feedback_rating_bar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.payment_option_reminder_config)).check(matches(isDisplayed()));
        onView(withId(R.id.fare_prompt)).check(matches(isDisplayed()));

        // Test for End of ride acknowledged case

        activityRule.getScenario().onActivity(rideActivity -> {
            rideActivity.rideViewModel.getRide().setRideStatus(Ride.END_ACKED);
            rideActivity.toggleEndOfRideDetailsPanel();
        });

        baseRobot.waitForView(withId(R.id.end_of_ride_details_panel), 4000, 500);
        onView(withId(R.id.end_of_ride_details_panel)).check(matches(isDisplayed()));
        onView(withId(R.id.submit_rating)).check(matches(isDisplayed()));
        onView(withId(R.id.feedback_rating_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.payment_option_reminder_config)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fare_prompt)).check(matches(not(isDisplayed())));

        // Previous Ride End Test (app closed before ride was cleared)

        activityRule.getScenario().onActivity(rideActivity -> {
            // Simulate end of Ride
            rideActivity.rideViewModel.resetRide();
            rideActivity.toggleEndOfRideDetailsPanel();
        });
        onView(withId(R.id.end_of_ride_details_panel)).check(matches(not(isDisplayed())));
    }

    @Test
    public void provideFeedback() {
        BaseRobot baseRobot = new BaseRobot();

        activityRule.getScenario().onActivity(rideActivity -> {
            // Simulate end of Ride
            rideActivity.rideViewModel.getRide().setRideStatus(Ride.END_ACKED);
            rideActivity.endOfRideAcknowledgedAction(true);
            rideActivity.feedbackRatingBar.setRating(3.5f);
        });

        baseRobot.waitForView(withId(R.id.submit_rating), 4000, 500);
        baseRobot.doOnView(allOf(isDisplayed(), isEnabled(), withId(R.id.submit_rating)), new CustomViewAction());


        baseRobot.waitForView(withId(R.id.ride_select_panel), 4000, 500);
        onView(withId(R.id.end_of_ride_details_panel)).check(matches(not(isDisplayed())));
        onView(withId(R.id.search_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.go_btn)).check(matches(isEnabled()));
    }
}
