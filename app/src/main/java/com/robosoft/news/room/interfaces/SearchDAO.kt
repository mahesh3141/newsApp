package com.robosoft.news.room.interfaces

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.robosoft.news.room.NewsTable
import com.robosoft.news.room.SearchTable

@Dao
interface SearchDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(vararg searchData: SearchTable)

    @Query("SELECT * FROM searchValue ORDER BY id DESC")
    suspend fun getSearch() : List<SearchTable>
}