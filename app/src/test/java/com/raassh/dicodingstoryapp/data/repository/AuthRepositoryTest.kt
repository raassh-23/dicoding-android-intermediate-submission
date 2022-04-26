@file:Suppress("DEPRECATION")

package com.raassh.dicodingstoryapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.raassh.dicodingstoryapp.MainCoroutineRule
import com.raassh.dicodingstoryapp.data.FakeApiService
import com.raassh.dicodingstoryapp.data.api.ApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var apiService: ApiService
    private lateinit var authRepository: AuthRepository

    private val name = "test"
    private val email = "test@mail.com"
    private val password = "test_password"

    @Before
    fun setUp() {
        apiService = FakeApiService()
        authRepository = AuthRepository(apiService)
    }

    @Test
    fun `when register with correct credential should return true`() = mainCoroutineRule.runBlockingTest {
        val response = authRepository.register(name, email, password)

        Assert.assertTrue(response)
    }

    @Test(expected = HttpException::class)
    fun `when register with invalid email should throw exception`() = mainCoroutineRule.runBlockingTest {
        authRepository.register(name, "invalidemail", password)
    }

    @Test(expected = HttpException::class)
    fun `when register with duplicate email should throw exception`() = mainCoroutineRule.runBlockingTest {
        authRepository.register(name, email, password)
        authRepository.register(name, email, password)
    }
}