package com.thisobeystudio.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.thisobeystudio.bakingapp.activities.MainActivity;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by thisobeystudio on 14/9/17.
 * Copyright: (c) 2017 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    // this test only will work with internet connection
    // and only will work properly on portrait(phones) mode due to visibility usage
    // on tablets should work OK on both portrait and landscape modes

    private static final String RECIPE_NUTELLA_PIE = "Nutella Pie";
    private static final String RECIPE_BROWNIES = "Brownies";
    private static final String RECIPE_YELLOW_CAKE = "Yellow Cake";
    private static final String RECIPE_CHEESECAKE = "Cheesecake";

    @Rule
    public final ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource1() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void checkApp_MainActivityTest() {

        // set recycler view visible
        onView(withId(R.id.main_activity_recycler_view))
                .perform(setVisibility(View.VISIBLE));

        // perform scrollToPosition 0 and check if "RECIPE_NUTELLA_PIE" TextView isDisplayed
        onView(withId(R.id.main_activity_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition(0));
        onView(withText(RECIPE_NUTELLA_PIE))
                .check(matches(isDisplayed()));

        // perform scrollToPosition 1 and check if "RECIPE_BROWNIES" TextView isDisplayed
        onView(withId(R.id.main_activity_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition(1));
        onView(withText(RECIPE_BROWNIES))
                .check(matches(isDisplayed()));

        // perform scrollToPosition 2 and check if "RECIPE_YELLOW_CAKE" TextView isDisplayed
        onView(withId(R.id.main_activity_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition(2));
        onView(withText(RECIPE_YELLOW_CAKE))
                .check(matches(isDisplayed()));

        // perform scrollToPosition 3 and check if "RECIPE_CHEESECAKE" TextView isDisplayed
        onView(withId(R.id.main_activity_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition(3));
        onView(withText(RECIPE_CHEESECAKE))
                .check(matches(isDisplayed()));

        // set recycler view visible
        onView(withId(R.id.main_activity_recycler_view))
                .perform(setVisibility(View.VISIBLE));

        // Perform RecyclerView ItemOnClick at pos 0
        onView(withId(R.id.main_activity_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // set recycler view visible
        onView(withId(R.id.ingredients_recycler_view))
                .perform(setVisibility(View.VISIBLE));

        // Check if ingredients_recycler_view isDisplayed
        onView(withId(R.id.ingredients_recycler_view))
                .check(matches(isDisplayed()));

        // Check if "Graham Cracker crumbs" TextView isDisplayed
        onView(withText("Graham Cracker crumbs"))
                .check(matches(isDisplayed()));

        // Check if ViewPager isDisplayed and swipeLeft
        onView(withId(R.id.view_pager))
                .check(matches(isDisplayed()))
                .perform(swipeLeft());

        // Check if player isDisplayed
        onView(withId(R.id.player))
                .check(matches(isDisplayed()));

        // Check if "Step 1" TextView isDisplayed
        onView(withText("Step 1"))
                .check(matches(isDisplayed()));

        // Check if ViewPager isDisplayed and swipeRight
        onView(withId(R.id.view_pager))
                .check(matches(isDisplayed()))
                .perform(swipeRight());

        // Check if "Graham Cracker crumbs" TextView isDisplayed again
        onView(withText("Graham Cracker crumbs"))
                .check(matches(isDisplayed()));

        // Click on ViewPager INGREDIENTS BUTTON
        onView(withText("INGREDIENTS"))
                .check(matches(isDisplayed()))
                .perform(click());

        // Check if "Graham Cracker crumbs" TextView isDisplayed again
        onView(withText("Graham Cracker crumbs"))
                .check(matches(isDisplayed()));

        // Click on ViewPager STEPS BUTTON
        onView(withText("STEPS"))
                .check(matches(isDisplayed()))
                .perform(click());

        // Check if "Step 1" TextView isDisplayed again
        onView(withText("Step 1"))
                .check(matches(isDisplayed()));

        onView(withId(R.id.widget_menu_action))
                .check(matches(isDisplayed()))
                .perform(click());

    }

    @After
    public void unregisterIdlingResource2() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }

    public static ViewAction setVisibility(final int visibility) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isEnabled();
            }

            @Override
            public String getDescription() {
                return "Set view visibility";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadUntilIdle();
                view.setVisibility(visibility);
                uiController.loopMainThreadUntilIdle();
            }
        };
    }
}