package com.robosoft.news.ui.home

import com.robosoft.news.network.ApiService

class HomeRepository(private val newsApi:ApiService.NewsApi) {
    suspend fun getTopHeadLine(country:String,apiKey:String)=newsApi.getTopHeadLine(country,apiKey)
    suspend fun getSearchResult(q:String,sortBy:String,apiKey: String)=newsApi.getEverything(q,sortBy,apiKey)
}