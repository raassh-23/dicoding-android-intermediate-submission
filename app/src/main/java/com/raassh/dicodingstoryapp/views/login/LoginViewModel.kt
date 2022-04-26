package com.raassh.dicodingstoryapp.views.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.raassh.dicodingstoryapp.data.api.ApiConfig
import com.raassh.dicodingstoryapp.data.api.GenericResponse
import com.raassh.dicodingstoryapp.data.api.LoginResponse
import com.raassh.dicodingstoryapp.misc.EspressoIdlingResource
import com.raassh.dicodingstoryapp.misc.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _token = MutableLiveData<Event<String>>()
    val token: LiveData<Event<String>> = _token

    private val _error = MutableLiveData<Event<String>>()
    val error: LiveData<Event<String>> = _error

    fun login(email: String, password: String) {
        _isLoading.value = true
        EspressoIdlingResource.increment()

        ApiConfig.getApiService().login(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                EspressoIdlingResource.decrement()

                if (response.isSuccessful) {
                    val token = response.body()?.loginResult?.token ?: ""
                    _token.value = Event(token)
                } else {
                    val errorBody = response.errorBody()

                    if (errorBody != null) {
                        val errorResponse = Gson().fromJson(
                            errorBody.charStream(),
                            GenericResponse::class.java
                        )

                        _error.value = Event(errorResponse.message)
                    } else {
                        _error.value = Event("")
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                EspressoIdlingResource.decrement()
                _error.value = Event(t.message.toString())
            }
        })
    }
}