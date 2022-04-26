package com.raassh.dicodingstoryapp

import com.raassh.dicodingstoryapp.data.api.ListStoryItem

object Dummy {
    fun getListStory(): List<ListStoryItem> {
        return mutableListOf<ListStoryItem>().apply {
            for (i in 1..10) {
                add(
                    ListStoryItem(
                        id = "Id $i",
                        name = "User $i",
                        photoUrl = "https://picsum.photos/200/300",
                        description = "Description $i",
                        createdAt = "2022-04-26T07:32:19.121Z",
                        lat = 0.0,
                        lon = 0.0
                    )
                )
            }
        }
    }
}