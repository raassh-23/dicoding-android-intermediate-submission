package com.raassh.dicodingstoryapp.views

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.raassh.dicodingstoryapp.MainCoroutineRule
import com.raassh.dicodingstoryapp.data.SessionPreferences
import com.raassh.dicodingstoryapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SharedViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var pref: SessionPreferences
    private lateinit var sharedViewModel: SharedViewModel

    private val token = "test123"

    @Before
    fun setUp() {
        sharedViewModel = SharedViewModel(pref)
    }

    @Test
    fun `when getToken should not null and return a string`() {
        val tokenFlow = flowOf(token)
        `when`(pref.getSavedToken()).thenReturn(tokenFlow)

        val actual = sharedViewModel.getToken().getOrAwaitValue()

        Assert.assertNotNull(actual)
        Assert.assertEquals(token, actual)
    }

    @Suppress("DEPRECATION")
    @Test
    fun `when saveToken should call saveToken of pref`() = mainCoroutineRule.runBlockingTest {
        sharedViewModel.saveToken(token)
        verify(pref).saveToken(token)
    }
}