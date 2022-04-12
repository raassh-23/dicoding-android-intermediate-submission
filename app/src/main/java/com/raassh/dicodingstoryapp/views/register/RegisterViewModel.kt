package com.raassh.dicodingstoryapp.views.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.raassh.dicodingstoryapp.data.api.ApiConfig
import com.raassh.dicodingstoryapp.data.api.GenericResponse
import com.raassh.dicodingstoryapp.misc.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val registerResult = MediatorLiveData<Result<String>>()

    fun register(name: String, email: String, password: String): LiveData<Result<String>> {
        registerResult.value = Result.Loading
        ApiConfig.getApiService().register(name, email, password)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful) {
                        registerResult.value = Result.Success(REGISTERED)
                    } else {
                        registerResult.value = Result.Error(response.message())
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    registerResult.value = Result.Error(t.message.toString())
                }
            })

        return registerResult
    }

    companion object {
        const val REGISTERED = "registered"
    }
}