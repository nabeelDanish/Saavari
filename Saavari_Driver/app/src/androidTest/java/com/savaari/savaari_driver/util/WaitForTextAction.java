package com.savaari.savaari_driver.util;

import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;

class WaitForTextAction implements ViewAction {
    private String text;
    private long timeout;

    public WaitForTextAction(String text, long timeout) {
        this.text = text;
        this.timeout = timeout;
    }

    @Override
    public Matcher<View> getConstraints() {
        return isAssignableFrom(TextView.class);
    }

    @Override
    public String getDescription() {
        return "wait up to $timeout milliseconds for the view to have text $text";
    }

    @Override
    public void perform(UiController uiController, View view) {
        long endTime = System.currentTimeMillis() + timeout;
        do {
            TextView textView = (TextView) view;
            if (textView.getText() == text) {
                return;
            }
            uiController.loopMainThreadForAtLeast(50);
        } while (System.currentTimeMillis() < endTime);
        try {
            throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
};
