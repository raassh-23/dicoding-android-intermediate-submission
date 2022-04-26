package com.raassh.dicodingstoryapp.views.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.raassh.dicodingstoryapp.data.repository.StoryRepository

class StoriesViewModel(storyRepository: StoryRepository) : ViewModel() {
    val stories = storyRepository.getStoriesPaged()

    @Suppress("UNCHECKED_CAST")
    class Factory(private val storyRepository: StoryRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StoriesViewModel(storyRepository) as T
        }
    }
}