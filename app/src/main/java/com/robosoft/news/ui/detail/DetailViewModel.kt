package com.robosoft.news.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel :ViewModel() {
    val errorMessage = MutableLiveData<String?>()

    val loading = MutableLiveData<Boolean>()
    private fun onError(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            errorMessage.value = message
            loading.value = false
            println("*** Error $message")
        }
    }
}