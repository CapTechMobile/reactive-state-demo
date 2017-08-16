package atownsend.reactivestate.main.ui

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingResource
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.replaceText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import atownsend.reactivestate.R
import atownsend.reactivestate.TestReactiveStateApplication
import atownsend.reactivestate.util.getStringFromFile
import com.jakewharton.espresso.OkHttp3IdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

  @Rule @JvmField val activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

  lateinit var mockWebServer: MockWebServer
  lateinit var idlingResource: IdlingResource

  @Before
  fun setup() {
    val app = InstrumentationRegistry.getTargetContext().applicationContext as TestReactiveStateApplication
    mockWebServer = app.mockWebServer
    idlingResource = OkHttp3IdlingResource.create("OkHttp", app.okHttpClient)
    Espresso.registerIdlingResources(idlingResource)
  }

  @After
  fun teardown() {
    Espresso.unregisterIdlingResources(idlingResource)
  }

  @Test
  fun testCheckUsername() {
    val responseString = InstrumentationRegistry.getContext().getStringFromFile(
        "check_username_success.json")
    mockWebServer.enqueue(MockResponse().setBody(responseString))

    onView(withId(R.id.editText)).perform(replaceText("alex-townsend"))
    onView(withId(R.id.helloText)).check(matches(withText("Username verified")))
  }

  @Test
  fun testSubmit() {
    val responseString = InstrumentationRegistry.getContext().getStringFromFile(
        "check_username_success.json")
    mockWebServer.enqueue(MockResponse().setBody(responseString))
    val submitString = InstrumentationRegistry.getContext().getStringFromFile("submit_success.json")
    mockWebServer.enqueue(MockResponse().setBody(submitString))

    onView(withId(R.id.editText)).perform(replaceText("alex-townsend"))
    onView(withId(R.id.button)).perform(click())
    onView(withId(R.id.helloText)).check(matches(withText("Username verified")))
    onView(withId(R.id.reposText)).check(matches(withText(
        "alex-townsend's repos include:\nAnimatedVectorDrawableExample\n" +
            "GettingStartedRxAndroid\nRemoteConfigSample\nRxBinding\nSwipeOpenItemTouchHelper")))
  }

}