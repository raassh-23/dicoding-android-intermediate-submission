package com.raassh.dicodingstoryapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.raassh.dicodingstoryapp.data.api.ListStoryItem
import com.raassh.dicodingstoryapp.data.database.StoryDao

class FakeStoryDao : StoryDao {
    private val stories = mutableListOf<ListStoryItem>()

    override suspend fun insertStories(stories: List<ListStoryItem>) {
        this.stories.addAll(stories)
    }

    override fun getAllStories(): PagingSource<Int, ListStoryItem> = FakePagingSource(stories)

    override suspend fun deleteAllStories() {
        stories.clear()
    }
}

class FakePagingSource(private val data: List<ListStoryItem>) : PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>) = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> =
        LoadResult.Page(data, null, null)
}