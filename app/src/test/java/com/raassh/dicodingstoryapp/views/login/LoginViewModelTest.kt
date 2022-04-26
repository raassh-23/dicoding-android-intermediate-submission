@file:Suppress("DEPRECATION")

package com.raassh.dicodingstoryapp.views.login

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
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var loginViewModel: LoginViewModel

    private val token = "test123"

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(authRepository)
    }

    @Test
    fun `when login with correct credential should set token`() =
        mainCoroutineRule.runBlockingTest {
            val email = "test@email.com"
            val password = "123456"
            `when`(authRepository.login(email, password)).thenReturn(token)

            loginViewModel.login(email, password)
            val actual = loginViewModel.token.getOrAwaitValue()

            Assert.assertNotNull(actual)
            Assert.assertEquals(token, actual.content)
        }

    @Test
    fun `when login with wrong password should set error`() = mainCoroutineRule.runBlockingTest {
        val email = "test@email.com"
        val password = "wrongpassword"
        val expectedMessage = "Invalid password"
        `when`(authRepository.login(email, password)).thenThrow(
            createErrorResponse(401, expectedMessage)
        )

        loginViewModel.login(email, password)
        val actual = loginViewModel.error.getOrAwaitValue()

        Assert.assertNotNull(actual)
        Assert.assertEquals(expectedMessage, actual.content)
    }

    @Test
    fun `when login with wrong email should set error`() = mainCoroutineRule.runBlockingTest {
        val email = "wrongtest@email.com"
        val password = "123456"
        val expectedMessage = "User not found"
        `when`(authRepository.login(email, password)).thenThrow(
            createErrorResponse(401, expectedMessage)
        )

        loginViewModel.login(email, password)
        val actual = loginViewModel.error.getOrAwaitValue()

        Assert.assertNotNull(actual)
        Assert.assertEquals(expectedMessage, actual.content)
    }
}