package com.robosoft.news.room

import android.content.Context
import androidx.room.*
import com.robosoft.news.di.Component
import com.robosoft.news.room.interfaces.NewsDAO
import com.robosoft.news.room.interfaces.SearchDAO

@Database(
    entities = arrayOf(NewsTable::class, SearchTable::class),
    version = 1,
    exportSchema = false
)
abstract class NewsDB : RoomDatabase() {

    abstract fun newsDAO(): NewsDAO

    abstract fun searchDAO(): SearchDAO

    companion object {

        @Volatile
        private var INSTANCE: NewsDB? = null

        fun getDBClient(context: Context) : NewsDB {

            if (INSTANCE != null) return INSTANCE!!

            synchronized(this) {
                INSTANCE = Room
                    .databaseBuilder(context, NewsDB::class.java, "NEWS_DATABASE")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }

    }

}