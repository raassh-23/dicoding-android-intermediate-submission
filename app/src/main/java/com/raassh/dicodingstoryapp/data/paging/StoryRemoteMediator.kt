package com.raassh.dicodingstoryapp.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.raassh.dicodingstoryapp.data.api.ApiService
import com.raassh.dicodingstoryapp.data.api.ListStoryItem
import com.raassh.dicodingstoryapp.data.database.StoryDatabase

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val auth: String
) : RemoteMediator<Int, ListStoryItem>() {
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStoryItem>
    ): MediatorResult {
        val page = INITIAL_PAGE

        try {
            val response = apiService
                .getAllStories(auth, page, state.config.pageSize)
                .listStory

            val endOfPagination = response.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.getStoryDao().deleteAllStories()
                }

                database.getStoryDao().insertStories(response)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPagination)
        } catch (ex: Exception) {
            return MediatorResult.Error(ex)
        }
    }

    companion object {
        const val INITIAL_PAGE = 1
    }
}