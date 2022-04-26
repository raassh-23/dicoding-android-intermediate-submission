@file:Suppress("DEPRECATION")

package com.raassh.dicodingstoryapp.views.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.raassh.dicodingstoryapp.MainCoroutineRule
import com.raassh.dicodingstoryapp.createErrorResponse
import com.raassh.dicodingstoryapp.data.repository.AuthRepository
import com.raassh.dicodingstoryapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var registerViewModel: RegisterViewModel

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(authRepository)
    }

    @Test
    fun `when register with correct credential should set isSuccess`() = mainCoroutineRule.runBlockingTest {
        val name = "test"
        val email = "test@email.com"
        val password = "123456"
        val expected = true
        Mockito.`when`(authRepository.register(name, email, password)).thenReturn(expected)

        registerViewModel.register(name, email, password)
        val actual = registerViewModel.isSuccess.getOrAwaitValue()

        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual.content)
    }

    @Test
    fun `when register with same email should set error`() = mainCoroutineRule.runBlockingTest {
        val name = "test"
        val email = "test@email.com"
        val password = "123456"
        val expected = "Email is already taken"
        Mockito.`when`(authRepository.register(name, email, password)).thenThrow(
            createErrorResponse(400, expected)
        )

        registerViewModel.register(name, email, password)
        val actual = registerViewModel.error.getOrAwaitValue()

        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual.content)
    }

    @Test
    fun `when register with wrong format email should set error`() = mainCoroutineRule.runBlockingTest {
        val name = "test"
        val email = "test@email.comwrong"
        val password = "123456"
        val expected = "email must be a valid email"
        Mockito.`when`(authRepository.register(name, email, password)).thenThrow(
            createErrorResponse(400, expected)
        )

        registerViewModel.register(name, email, password)
        val actual = registerViewModel.error.getOrAwaitValue()

        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual.content)
    }
}