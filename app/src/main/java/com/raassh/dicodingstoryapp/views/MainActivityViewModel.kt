package com.raassh.dicodingstoryapp.views

import androidx.lifecycle.*
import com.raassh.dicodingstoryapp.data.SessionPreferences
import kotlinx.coroutines.launch

class MainActivityViewModel(private val pref: SessionPreferences) : ViewModel() {
    fun getToken(): LiveData<String> {
        return pref.getSavedToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val pref: SessionPreferences) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainActivityViewModel(pref) as T
        }
    }
}