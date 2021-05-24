package com.savaari.savaari_rider.utility;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;

// Click a view without the espresso-enforced condition that 90% of a View should be visible
// TODO: hacky way to get around inability to remove Animations, use IdlingResources
public class CustomViewAction implements ViewAction {
    @Override
    public Matcher<View> getConstraints() {
        return isEnabled(); // no constraints, they are checked above
    }

    @Override
    public String getDescription() {
        return "same description";
    }

    @Override
    public void perform(UiController uiController, View view) {
        view.performClick();
    }
}
