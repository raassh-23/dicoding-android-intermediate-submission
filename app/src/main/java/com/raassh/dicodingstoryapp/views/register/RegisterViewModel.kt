package com.raassh.dicodingstoryapp.views.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raassh.dicodingstoryapp.data.api.ApiConfig
import com.raassh.dicodingstoryapp.data.api.GenericResponse
import com.raassh.dicodingstoryapp.misc.Event
import com.raassh.dicodingstoryapp.misc.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val _result = MutableLiveData<Event<Result<String>>>()
    val result: LiveData<Event<Result<String>>> = _result

    fun register(name: String, email: String, password: String) {
        _result.value = Event(Result.Loading)
        ApiConfig.getApiService().register(name, email, password)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        _result.value = Event(Result.Success(REGISTERED))
                    } else {
                        _result.value = Event(Result.Error(response.message()))
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    _result.value = Event(Result.Error(t.message.toString()))
                }

            })
    }

    companion object {
        const val REGISTERED = "registered"
    }
}