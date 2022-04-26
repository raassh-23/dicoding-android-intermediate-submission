package com.raassh.dicodingstoryapp.data

import com.raassh.dicodingstoryapp.Dummy
import com.raassh.dicodingstoryapp.createErrorResponse
import com.raassh.dicodingstoryapp.data.api.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService : ApiService {
    private val users = mutableListOf<Map<String, String>>()

    override suspend fun register(name: String, email: String, password: String): GenericResponse {
        if (!EMAIL_REGEX.toRegex().matches(email)) {
            throw createErrorResponse(400, "Email must be a valid email")
        }

        val userExists = users.find {
            it["email"] == email
        }

        if (userExists != null) {
            throw createErrorResponse(400, "Email is already taken")
        }

        val user = mapOf(
            "name" to name,
            "email" to email,
            "password" to password
        )

        users.add(user)

        return GenericResponse(
            error = false,
            message = "User created"
        )
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        val userExists = users.find {
            it["email"] == email
        } ?: throw createErrorResponse(401, "User not found")

        if (userExists["password"] == password) {
            return LoginResponse(
                error = false,
                message = "Login success",
                loginResult = LoginResult(
                    name = userExists["name"] as String,
                    userId = "test_id",
                    token = "test_token_123"
                )
            )
        } else {
            throw createErrorResponse(401, "Invalid password")
        }
    }

    override suspend fun getAllStories(token: String, location: Int) = StoriesResponse(
        error = false,
        listStory = Dummy.getListStory(),
        message = "Get All Stories"
    )

    override suspend fun getAllStoriesPaged(token: String, page: Int, size: Int) = StoriesResponse(
        error = false,
        listStory = Dummy.getListStory(),
        message = "Get All Stories Paged"
    )

    override suspend fun addStory(
        file: MultipartBody.Part,
        params: HashMap<String, RequestBody>,
        auth: String
    ) = GenericResponse(
        error = false,
        message = "Story added"
    )

    companion object {
        private const val EMAIL_REGEX = "^[A-Za-z](.*)([@])(.+)(\\.)(.+)"
    }
}