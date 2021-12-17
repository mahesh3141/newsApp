package com.robosoft.news.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BookMarkFactory(private val repository: BookMarkRepository) :ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BookMarkViewModel::class.java)){
            this.repository?.let { BookMarkViewModel(it) } as T
        }else{
            throw IllegalAccessException("unknown view model")
        }
    }
}