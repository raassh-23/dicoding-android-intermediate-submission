package com.raassh.dicodingstoryapp

import com.raassh.dicodingstoryapp.data.api.GenericResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

fun createErrorResponse(code: Int, message: String) = HttpException(
    Response.error<GenericResponse>(
        code,
        "{\"error\":true,\"message\":\"$message\"}"
            .toResponseBody("application/json".toMediaType())
    )
)