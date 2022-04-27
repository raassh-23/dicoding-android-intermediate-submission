@file:Suppress("DEPRECATION")

package com.raassh.dicodingstoryapp.views.storieswithmaps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.raassh.dicodingstoryapp.Dummy
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class StoriesWithMapViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var storiesWithMapViewModel: StoriesWithMapViewModel

    @Before
    fun setUp() {
        storiesWithMapViewModel = StoriesWithMapViewModel(storyRepository)
    }

    @Test
    fun `when getAllStories should set stories`() = mainCoroutineRule.runBlockingTest {
        val expected = Dummy.getListStory()

        `when`(storyRepository.getStoriesWithLocation()).thenReturn(expected)

        storiesWithMapViewModel.getAllStories()
        val actual = storiesWithMapViewModel.stories.getOrAwaitValue()

        Assert.assertNotNull(actual)
        Assert.assertEquals(expected.size, actual.size)
        Assert.assertEquals(expected[0], actual[0])
    }

    @Test
    fun `when getAllStories throw exception should set error`() = mainCoroutineRule.runBlockingTest {
        val expected = "error"

        `when`(storyRepository.getStoriesWithLocation()).thenThrow(createErrorResponse(500, expected))

        storiesWithMapViewModel.getAllStories()
        val actual = storiesWithMapViewModel.error.getOrAwaitValue()

        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual.content)
    }
}