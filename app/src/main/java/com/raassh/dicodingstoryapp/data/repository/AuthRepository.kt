package com.raassh.dicodingstoryapp.data.repository

import com.raassh.dicodingstoryapp.data.api.ApiService

class AuthRepository(
    private val apiService: ApiService
) {
    suspend fun login(email: String, password: String) =
        apiService.login(email, password).loginResult.token

    suspend fun register(name: String, email: String, password: String) =
        !apiService.register(name, email, password).error
}