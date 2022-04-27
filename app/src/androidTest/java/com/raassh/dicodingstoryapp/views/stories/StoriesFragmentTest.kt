package com.raassh.dicodingstoryapp.views.stories

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.raassh.dicodingstoryapp.JsonConverter
import com.raassh.dicodingstoryapp.R
import com.raassh.dicodingstoryapp.data.api.ApiConfig
import com.raassh.dicodingstoryapp.misc.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class StoriesFragmentTest {

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun getAllStories_Success() {
        val bundle = bundleOf(
            "token" to "test_123",
            "newStoryAdded" to false
        )

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("stories_success_response.json"))
        mockWebServer.enqueue(mockResponse)

        launchFragmentInContainer<StoriesFragment>(bundle, R.style.Theme_DicodingStoryApp)

        onView(withId(R.id.list_story)).check(matches(isDisplayed()))

        onView(withId(R.id.list_story))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText("By mama"))
                )
            )

        onView(withId(R.id.list_story))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText("By wewewe"))
                )
            )
    }

    @Test
    fun getAllStories_Failed() {
        val bundle = bundleOf(
            "token" to "test_123",
            "newStoryAdded" to false
        )

        val mockResponse = MockResponse()
            .setResponseCode(500)
        mockWebServer.enqueue(mockResponse)

        launchFragmentInContainer<StoriesFragment>(bundle, R.style.Theme_DicodingStoryApp)

        onView(withId(R.id.error_text_initial))
            .check(matches(isDisplayed()))

    }
}