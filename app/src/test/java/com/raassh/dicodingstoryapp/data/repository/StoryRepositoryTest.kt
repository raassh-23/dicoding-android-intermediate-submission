@file:Suppress("DEPRECATION")

package com.raassh.dicodingstoryapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.raassh.dicodingstoryapp.Dummy
import com.raassh.dicodingstoryapp.MainCoroutineRule
import com.raassh.dicodingstoryapp.data.FakeApiService
import com.raassh.dicodingstoryapp.data.FakeStoryDao
import com.raassh.dicodingstoryapp.data.api.ApiService
import com.raassh.dicodingstoryapp.data.database.StoryDao
import com.raassh.dicodingstoryapp.data.database.StoryDatabase
import com.raassh.dicodingstoryapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var apiService: ApiService

    @Mock
    private lateinit var storyDatabase: StoryDatabase
    private lateinit var storyDao: StoryDao
    private lateinit var storyRepository: StoryRepository

    private val stories = Dummy.getListStory()

    @Before
    fun setUp() {
        apiService = FakeApiService()
        storyDao = FakeStoryDao()
        storyRepository = StoryRepository(storyDatabase, apiService, "bearer token")
    }

    @Test
    fun `when addNewStory should return true`() =
        mainCoroutineRule.runBlockingTest {
            val actual = storyRepository.addNewStory(
                MultipartBody.Part.create(
                    Headers.headersOf(),
                    "".toRequestBody("text/plain".toMediaType())
                ),
                HashMap()
            )

            assertTrue(actual)
        }

    @Test
    fun `when getStoriesWithLocation should not null`() = mainCoroutineRule.runBlockingTest {
        val actual = storyRepository.getStoriesWithLocation()

        assertNotNull(actual)
        assertEquals(stories.size, actual.size)
        assertEquals(stories[0], actual[0])
    }

    @Test
    fun `when getStoriesPaged should not null`() = mainCoroutineRule.runTest {
        `when`(storyDatabase.getStoryDao()).thenReturn(storyDao)

        val actual = storyRepository.getStoriesPaged().getOrAwaitValue()

        assertNotNull(actual)
    }
}