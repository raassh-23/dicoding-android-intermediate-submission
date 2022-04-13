package com.raassh.dicodingstoryapp.views.newstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.raassh.dicodingstoryapp.data.api.ApiConfig
import com.raassh.dicodingstoryapp.data.api.GenericResponse
import com.raassh.dicodingstoryapp.misc.Event
import com.raassh.dicodingstoryapp.misc.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class NewStoryViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Event<Boolean>>()
    val isSuccess: LiveData<Event<Boolean>> = _isSuccess

    private val _error = MutableLiveData<Event<String>>()
    val error: LiveData<Event<String>> = _error

    fun addNewStory(image: File, description: String, token: String) {
        val reducedImage = reduceFileImage(image)

        val descPart = description.toRequestBody("text/plain".toMediaType())
        val imageMultiPart = MultipartBody.Part.createFormData(
            "photo",
            reducedImage.name,
            reducedImage.asRequestBody("image/jpeg".toMediaType())
        )
        val auth = "Bearer $token"

        _isLoading.value = true
        ApiConfig.getApiService().addStory(imageMultiPart, descPart, auth)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    _isLoading.value = false

                    if (response.isSuccessful) {
                        _isSuccess.value = Event(true)
                    } else {
                        val errorResponse = Gson().fromJson(
                            response.errorBody()!!.charStream(),
                            GenericResponse::class.java
                        )
                        _error.value = Event(errorResponse.message)
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    _isLoading.value = false
                    _error.value = Event(t.message.toString())
                }
            })
    }
}