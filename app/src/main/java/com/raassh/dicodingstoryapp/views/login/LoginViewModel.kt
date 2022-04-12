package com.raassh.dicodingstoryapp.views.login

import androidx.lifecycle.LiveData
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
    private val _result = MutableLiveData<Event<Result<String>>>()
    val result: LiveData<Event<Result<String>>> = _result

    fun login(email: String, password: String) {
        _result.value = Event(Result.Loading)
        ApiConfig.getApiService().login(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.loginResult?.token ?: ""
                    _result.value = Event(Result.Success(token))
                } else {
                    _result.value = Event(Result.Error(response.message()))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _result.value = Event(Result.Error(t.message.toString()))
            }
        })

    }
}