package com.robosoft.news.di

import com.robosoft.news.interfaces.AddBookMark
import com.robosoft.news.ui.home.adapters.HomeAdapter
import org.koin.dsl.bind
import org.koin.dsl.module

val interfaceModule = module {

   factory { HomeAdapter(get(),get(),get(),get()) } bind AddBookMark::class
}