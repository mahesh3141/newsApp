package com.robosoft.news.di


import com.robosoft.news.room.NewsTable
import com.robosoft.news.room.SearchTable
import org.koin.dsl.module

val diModule = module {

    factory { NewsTable() }
    factory { SearchTable() }

}