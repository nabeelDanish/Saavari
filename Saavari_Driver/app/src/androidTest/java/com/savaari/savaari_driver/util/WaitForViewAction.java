package com.savaari.savaari_driver.util;

import android.util.Log;
import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.util.TreeIterables;

import org.hamcrest.Matcher;

import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static java.lang.Thread.sleep;

public class WaitForViewAction implements ViewAction {

    private Matcher<View> matcher;

    public WaitForViewAction(Matcher<View> matcher) {
        this.matcher = matcher;
    }

    @Override
    public Matcher<View> getConstraints() {
        return isRoot();
    }

    @Override
    public String getDescription() {
        return "searching for view $matcher in the root view";
    }

    @Override
    public void perform(UiController uiController, View view) {
        Log.d("TAG", "perform: Running Tries!");
        long endTime = System.currentTimeMillis() + 15000;
        Iterable<View> childViews = TreeIterables.breadthFirstViewTraversal(view);
        do {
            childViews.forEach((it) -> {
                if (matcher.matches(it) && (it.getVisibility() == View.VISIBLE)) {
                    Log.d("TAG", "perform: FOUND");
                    return;
                }
            });
            Log.d("TAG", "perform: TRYING AGAIN!");
            uiController.loopMainThreadForAtLeast(50);
        } while (System.currentTimeMillis() < endTime);
        Log.d("TAG", "perform: NOT FOUND");
    }
}
