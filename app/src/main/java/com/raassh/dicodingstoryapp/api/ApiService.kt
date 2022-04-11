package com.raassh.dicodingstoryapp.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.POST

interface ApiService {
    @POST("/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<GenericResponse>

    @POST("/login")
    fun register(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>
}