package com.dicoding.storyapp.ui

import androidx.paging.ExperimentalPagingApi
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.utils.JsonConverter
import com.dicoding.storyapp.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalPagingApi
@MediumTest
@HiltAndroidTest
class LocationFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
        ApiConfig.API_BASE_URL_MOCK = "http://127.0.0.1:8080/"
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun launchLocationFragment_Success() {
        launchFragmentInHiltContainer<LocationFragment>()

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)

        Espresso.onView(ViewMatchers.withId(R.id.map))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}