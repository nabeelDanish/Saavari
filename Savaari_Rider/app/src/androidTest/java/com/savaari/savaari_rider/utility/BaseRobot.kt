package com.savaari.savaari_rider.utility

import android.os.SystemClock.sleep
import android.util.Log
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.*
import com.savaari.savaari_rider.utility.EspressoExtensions.Companion.searchFor
import org.hamcrest.Matcher


open class BaseRobot {

    fun doOnView(matcher: Matcher<View>, vararg actions: ViewAction) {
        var tries = 0

        tries = 0
        while (tries < 10) {
            tries++;
            actions.forEach {
                try {
                    waitForView(matcher).perform(it);
                    return@doOnView
                }
                catch (e : java.lang.Exception) {
                    e.printStackTrace()
                    // Do nothing
                }
            }

            sleep(1000);
        }
    }

    fun assertOnView(matcher: Matcher<View>, vararg assertions: ViewAssertion) {
        assertions.forEach {
            waitForView(matcher).check(it)
        }
    }

    /**
     * Perform action of implicitly waiting for a certain view.
     * This differs from EspressoExtensions.searchFor in that,
     * upon failure to locate an element, it will fetch a new root view
     * in which to traverse searching for our @param match
     *
     * @param viewMatcher ViewMatcher used to find our view
     */
    fun waitForView(
            viewMatcher: Matcher<View>,
            waitMillis: Int = 5000,
            waitMillisPerTry: Long = 100
    ): ViewInteraction {

        // Derive the max tries
        val maxTries = waitMillis / waitMillisPerTry.toInt()

        var tries = 0

        for (i in 0..maxTries)
            try {
                // Track the amount of times we've tried
                tries++

                // Search the root for the view
                onView(isRoot()).perform(searchFor(viewMatcher))
                // If we're here, we found our view. Now return it
                return onView(viewMatcher)

            } catch (e: Exception) {

                if (tries == maxTries) {
                    throw e
                }
                sleep(waitMillisPerTry)
            }

        throw Exception("Error finding a view matching $viewMatcher")
    }
}