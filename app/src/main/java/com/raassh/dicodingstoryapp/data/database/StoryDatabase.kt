package com.raassh.dicodingstoryapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.raassh.dicodingstoryapp.data.api.ListStoryItem

@Database(entities = [ListStoryItem::class, RemoteKeyEntity::class], version = 1)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun getStoryDao(): StoryDao
    abstract fun getRemoteKeyDao(): RemoteKeyDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            return INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                StoryDatabase::class.java, "story_database"
            ).build().also {
                INSTANCE = it
            }
        }
    }
}