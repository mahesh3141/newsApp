package com.robosoft.news.di

import com.robosoft.news.room.NewsTable
import com.robosoft.news.room.SearchTable
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class Component :KoinComponent {

    val objNewsTable :NewsTable by inject()
    val objSearchTable:SearchTable by inject()


}