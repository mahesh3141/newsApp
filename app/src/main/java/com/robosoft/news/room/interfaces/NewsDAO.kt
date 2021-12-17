package com.robosoft.news.room.interfaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.robosoft.news.room.NewsTable

@Dao
interface NewsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(vararg newsTable: NewsTable)

    @Query("DELETE  FROM news WHERE id =:ids")
    suspend fun deleteBookMark(ids: Int?)

    @Query("SELECT * FROM news ORDER BY id DESC")
    suspend fun getBookmark(): List<NewsTable>


}