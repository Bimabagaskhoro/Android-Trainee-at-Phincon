package com.bimabagaskhoro.taskappphincon

import android.os.SystemClock
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.runner.AndroidJUnit4
import com.bimabagaskhoro.taskappphincon.feature.activity.AuthActivity
import com.bimabagaskhoro.taskappphincon.feature.activity.MainActivity
import com.bimabagaskhoro.taskappphincon.feature.auth.LoginFragment
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthActivityTest {
    @get:Rule
    val activity = ActivityScenarioRule(AuthActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }


    @Test
    fun a_login_success() {
        Intents.init()
        onView(withId(R.id.edt_email_login))
            .perform(typeText("yudakeling3@gmail.com"), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.edt_password_login))
            .perform(typeText("123456"), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.btn_login_fragment)).perform(click())
        SystemClock.sleep(6000)
        Intents.intended(hasComponent(MainActivity::class.java.name))
        onView(withId(R.id.rv_product_home)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun c_register_success() {
        val randomString = String()
        Intents.init()
        onView(withId(R.id.btn_signup_on_fragment_login)).perform(click())
        onView(withId(R.id.edt_email_register_fragment))
            .perform(typeText("uites999@gmail.com"), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.edt_password_register_fragment))
            .perform(typeText("123456"), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.edt_confirm_password_register_fragment))
            .perform(typeText("123456"), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.edt_name_register_fragment))
            .perform(typeText("uitest"), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.edt_phone_register_fragment))
            .perform(typeText("1234567890"), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.btn_register_register_fragment)).perform(click())
        SystemClock.sleep(2500)
//        Intents.intended(hasComponent(LoginActivity::class.java.name))
//        loginFragment.onFragment

        onView(withId(R.id.btn_login_on_register_fragment)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }





}