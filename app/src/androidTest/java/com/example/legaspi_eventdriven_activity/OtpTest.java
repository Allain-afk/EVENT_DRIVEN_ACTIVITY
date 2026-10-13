package com.example.legaspi_eventdriven_activity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class OtpTest {

    @Rule
    public ActivityScenarioRule<OtpActivity> activityRule =
            new ActivityScenarioRule<>(OtpActivity.class);

    @Test
    public void invalidOtp_showsErrorMessage() {
        // 1. Invalid OTP: Type an incorrect OTP code, close soft keyboard, click verify button
        onView(withId(R.id.etOtp))
                .perform(typeText("000000"), closeSoftKeyboard());

        onView(withId(R.id.btnVerifyOtp))
                .perform(click());

        // Assert that the error message view appears with the expected text
        // Note: The logic in OtpActivity sets the error on the TextInputLayout for invalid OTP
        onView(withId(R.id.tilOtp))
                .check(matches(hasDescendant(withText(R.string.error_otp_invalid))));
    }

    @Test
    public void validOtp_showsDashboard() {
        // Retrieve the dynamically generated correct OTP from the activity using reflection
        String validOtp = getGeneratedOtp(activityRule.getScenario());

        // 2. Valid OTP: Type the correct OTP code, close soft keyboard, click verify button
        onView(withId(R.id.etOtp))
                .perform(typeText(validOtp), closeSoftKeyboard());

        onView(withId(R.id.btnVerifyOtp))
                .perform(click());

        // Assert that the dashboard view successfully appears (HomeActivity's Title)
        onView(withId(R.id.tvTitle))
                .check(matches(isDisplayed()));
        onView(withId(R.id.tvTitle))
                .check(matches(withText(R.string.home_title)));
    }

    // Helper method to retrieve the dynamically generated OTP
    private String getGeneratedOtp(ActivityScenario<OtpActivity> scenario) {
        final String[] otp = new String[1];
        scenario.onActivity(activity -> {
            try {
                Field field = OtpActivity.class.getDeclaredField("currentOtp");
                field.setAccessible(true);
                otp[0] = (String) field.get(activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return otp[0];
    }
}
