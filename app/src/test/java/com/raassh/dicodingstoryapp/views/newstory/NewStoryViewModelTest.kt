@file:Suppress("DEPRECATION")

package com.raassh.dicodingstoryapp.views.newstory

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.raassh.dicodingstoryapp.MainCoroutineRule
import com.raassh.dicodingstoryapp.createErrorResponse
import com.raassh.dicodingstoryapp.data.repository.StoryRepository
import com.raassh.dicodingstoryapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class NewStoryViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var newStoryViewModel: NewStoryViewModel

    private val image = File("test")
    private val desc = "desc tes"

    @Mock
    private lateinit var location: Location

    @Before
    fun setUp() {
        newStoryViewModel = NewStoryViewModel(storyRepository)
    }

    @Test
    fun `when addStory without location should set isSuccess`() =
        mainCoroutineRule.runBlockingTest {
            val expected = true

            `when`(
                storyRepository.addNewStory(
                    org.mockito.kotlin.any(),
                    org.mockito.kotlin.any()
                )
            ).thenReturn(expected)

            newStoryViewModel.addNewStory(image, desc, compress = false)
            val actual = newStoryViewModel.isSuccess.getOrAwaitValue()

            Assert.assertNotNull(actual)
            Assert.assertEquals(expected, actual.content)
        }

    @Test
    fun `when addStory with location should set isSuccess`() = mainCoroutineRule.runBlockingTest {
        val expected = true

        `when`(location.latitude)
            .thenReturn(0.0)
        `when`(location.longitude)
            .thenReturn(0.0)
        `when`(
            storyRepository.addNewStory(
                org.mockito.kotlin.any(),
                org.mockito.kotlin.any()
            )
        ).thenReturn(expected)

        newStoryViewModel.addNewStory(image, desc, location, compress = false)
        val actual = newStoryViewModel.isSuccess.getOrAwaitValue()

        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual.content)
    }

    @Test
    fun `when addStory throw exception should set error`() = mainCoroutineRule.runBlockingTest {
        val expected = "error"

        `when`(
            storyRepository.addNewStory(
                org.mockito.kotlin.any(),
                org.mockito.kotlin.any()
            )
        ).thenThrow(createErrorResponse(500, expected))

        newStoryViewModel.addNewStory(image, desc, compress = false)
        val actual = newStoryViewModel.error.getOrAwaitValue()

        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual.content)
    }
}