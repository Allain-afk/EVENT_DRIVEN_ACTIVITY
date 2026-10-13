package com.example.legaspi_eventdriven_activity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    // 1. Use MainActivity which contains the login logic in your project
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void invalidLogin_keepsUserOnScreen() throws InterruptedException {
        // Use the exact IDs from your activity_main.xml
        onView(withId(R.id.etEmail))
                .perform(typeText("wrong@example.com"), closeSoftKeyboard());

        onView(withId(R.id.etPassword))
                .perform(typeText("wrongpassword"), closeSoftKeyboard());

        onView(withId(R.id.btnLogin))
                .perform(click());

        // Wait a bit for the Retrofit network call to fail/finish
        // Note: For production apps, use Espresso IdlingResource instead of Thread.sleep
        Thread.sleep(2000); 

        // Verify we are still on the login screen
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
    }

    @Test
    public void validLogin_showsOtpScreen() throws InterruptedException {
        // NOTE: Replace these credentials with valid ones from your backend/API
        onView(withId(R.id.etEmail))
                .perform(typeText("john.doe@example.com"), closeSoftKeyboard());

        onView(withId(R.id.etPassword))
                .perform(typeText("password123"), closeSoftKeyboard());

        onView(withId(R.id.btnLogin))
                .perform(click());

        // Wait a bit for the Retrofit network call to succeed
        Thread.sleep(2000);

        // Verify OtpActivity successfully appeared by checking for one of its views
        onView(withId(R.id.btnVerifyOtp)).check(matches(isDisplayed()));
    }
}