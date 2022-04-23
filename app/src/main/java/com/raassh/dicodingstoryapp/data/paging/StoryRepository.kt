package com.raassh.dicodingstoryapp.data.paging

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.raassh.dicodingstoryapp.data.api.ApiService
import com.raassh.dicodingstoryapp.data.api.ListStoryItem
import com.raassh.dicodingstoryapp.data.database.StoryDatabase

class StoryRepository(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val auth: String
)  {
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(database, apiService, auth),
            pagingSourceFactory = {
                database.getStoryDao().getAllStories()
            }
        ).liveData
    }
}