package com.savaari.savaari_rider.utility

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.Matcher
import java.lang.Thread.sleep

class EspressoExtensions {

    companion object {

        /**
         * Perform action of waiting for a certain view within a single root view
         * @param matcher Generic Matcher used to find our view
         */
        fun searchFor(matcher: Matcher<View>): ViewAction {

            return object : ViewAction {

                override fun getConstraints(): Matcher<View> {
                    return isRoot()
                }

                override fun getDescription(): String {
                    return "searching for view $matcher in the root view"
                }

                override fun perform(uiController: UiController, view: View) {

                    var tries = 0
                    val childViews: Iterable<View> = TreeIterables.breadthFirstViewTraversal(view)

                    while (tries < 10) {

                        // Look for the match in the tree of childviews
                        childViews.forEach {
                            if (matcher.matches(it).and(it.visibility == View.VISIBLE)) {
                                    return
                            }
                        }

                        tries++;
                        sleep(2000)

                    }
                }

            }
        }
    }
}