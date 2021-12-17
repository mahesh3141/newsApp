package com.robosoft.news.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeFactory(private val repository: HomeRepository?):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            this.repository?.let { HomeViewModel(it) } as T
        }else{
            throw IllegalAccessException("unknown View model")
        }
    }
}