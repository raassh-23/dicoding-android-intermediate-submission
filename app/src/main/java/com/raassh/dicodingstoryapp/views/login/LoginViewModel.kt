package com.raassh.dicodingstoryapp.views.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raassh.dicodingstoryapp.data.api.ApiConfig
import com.raassh.dicodingstoryapp.data.api.LoginResponse
import com.raassh.dicodingstoryapp.misc.Event
import com.raassh.dicodingstoryapp.misc.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val loginResult = MediatorLiveData<Result<String>>()

    fun login(email: String, password: String): LiveData<Result<String>> {
        loginResult.value = Result.Loading
        ApiConfig.getApiService().login(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.loginResult?.token ?: ""
                    loginResult.value = Result.Success(token)
                } else {
                    loginResult.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResult.value = Result.Error(t.message.toString())
            }
        })

        return loginResult
    }
}