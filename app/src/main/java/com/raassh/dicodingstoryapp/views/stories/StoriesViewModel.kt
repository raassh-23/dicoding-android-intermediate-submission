package com.raassh.dicodingstoryapp.views.stories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.raassh.dicodingstoryapp.data.api.ApiConfig
import com.raassh.dicodingstoryapp.data.api.GenericResponse
import com.raassh.dicodingstoryapp.data.api.ListStoryItem
import com.raassh.dicodingstoryapp.data.api.StoriesResponse
import com.raassh.dicodingstoryapp.misc.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoriesViewModel(private val token: String) : ViewModel() {
    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<Event<String>>()
    val error: LiveData<Event<String>> = _error

    init {
        getAllStories()
    }

    fun getAllStories() {
        _isLoading.value = true

        ApiConfig.getApiService().getAllStories("Bearer $token")
            .enqueue(object : Callback<StoriesResponse> {
                override fun onResponse(
                    call: Call<StoriesResponse>,
                    response: Response<StoriesResponse>
                ) {
                    _isLoading.value = false

                    if (response.isSuccessful) {
                        _stories.value = response.body()?.listStory
                    } else {
                        val errorResponse = Gson().fromJson(response.errorBody()!!.charStream(), GenericResponse::class.java)
                        _error.value = Event(errorResponse.message)
                    }
                }

                override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                    _isLoading.value = false
                    _error.value = Event(t.message.toString())
                }
            })
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val token: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StoriesViewModel(token) as T
        }
    }
}