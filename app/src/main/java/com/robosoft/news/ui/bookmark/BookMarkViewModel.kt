package com.robosoft.news.ui.bookmark

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.robosoft.news.room.NewsDB
import com.robosoft.news.room.NewsTable
import com.robosoft.news.room.SearchTable
import com.robosoft.news.ui.home.HomeViewModel
import com.robosoft.news.ui.home.models.Article
import kotlinx.coroutines.*
import okhttp3.Dispatcher

class BookMarkViewModel(private var repository: BookMarkRepository) : ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()
    var job: Job? = null
    var database: NewsDB? = null
    var newTable: MutableLiveData<ArrayList<NewsTable>>? = null
    var objNews: ArrayList<NewsTable>? = null
    var newDataSet = MutableLiveData<List<NewsTable>>()
    var deleteRecord = MutableLiveData<String?>()
    var searchDataSet = MutableLiveData<List<SearchTable>>()
    companion object {
        fun initializeDB(context: Context): NewsDB? {
            return NewsDB.getDBClient(context)
        }
    }

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private fun onError(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            errorMessage.value = message
            loading.value = false
            println("*** Error $message")
        }
    }


    fun getAllBookmarks(context: Context): LiveData<List<NewsTable>>? {
        database = initializeDB(context)
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val data = database?.newsDAO()?.getBookmark()
            data?.let {
                withContext(Dispatchers.Main) {
                    newDataSet.value = it

                    loading.value = false
                }
            }
        }
        return newDataSet
    }

    fun removeBookMark(context: Context, id: Int?) {

        database = initializeDB(context)
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val data = database?.newsDAO()?.deleteBookMark(id)

            withContext(Dispatchers.Main) {
                if (data != null) {
                    deleteRecord.value = "success"
                }else{
                    deleteRecord.value = "fail"
                }
                loading.value = false
            }

        }

    }


    fun addSearch(context: Context?, value: String?) {
        database = context?.let { initializeDB(it) }
        loading.value = true
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val searchTB = SearchTable(value)
            database?.searchDAO()?.insertSearch(searchTB)
            withContext(Dispatchers.Main){
                loading.value=false
            }
        }
    }

    fun getAllSearch(context: Context): LiveData<List<SearchTable>>? {
        database = initializeDB(context)
        loading.value = true
        job = CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val data = database?.searchDAO()?.getSearch()
            data?.let {
                withContext(Dispatchers.Main) {
                    searchDataSet.value = it
                    loading.value=false
                }
            }
        }

        return searchDataSet
    }
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }


}