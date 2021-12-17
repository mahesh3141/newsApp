package com.robosoft.news.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.robosoft.news.room.interfaces.NewsDAO
import com.robosoft.news.room.interfaces.SearchDAO

import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class NewsDBTest :TestCase(){

    private lateinit var db: NewsDB
    private lateinit var newsDAO: NewsDAO
    private lateinit var searchDAO: SearchDAO

    @Before
  public  override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context,NewsDB::class.java).build()
        newsDAO = db.newsDAO()
        searchDAO = db.searchDAO()
    }

    @After
    fun closeDB(){
        db.close()
    }

    @Test
    fun writeAndReadNews() = runBlocking{
        val objNews = NewsTable("Mahesh","kotest","writing kotest","begginer","Kotest",
        "www.kotest.io","www.google.com","kotlin")
        newsDAO.insertBookmark(objNews)

        val result = newsDAO.getBookmark()
        assertThat(result.contains(objNews)).isTrue()
    }

    @Test
    fun saveSearchData() = runBlocking {
        val objSearch = SearchTable("Nashik")
        searchDAO.insertSearch(objSearch)
        val result = searchDAO.getSearch()
        assertThat(result.contains(objSearch)).isTrue()
    }
}