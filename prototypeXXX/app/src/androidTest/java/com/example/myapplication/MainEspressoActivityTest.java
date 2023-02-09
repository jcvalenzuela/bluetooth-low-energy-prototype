package com.example.myapplication;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.widget.WithHint;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class MainEspressoActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activity = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testEditText() throws InterruptedException {

      onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
        onView(withText("Register")).perform(click());
       // Thread.sleep(2000);
//        onView(withClassName(Matchers.equalTo(Button.class.getName()))).check(matches(isDisplayed()));


    }
}
