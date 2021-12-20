package com.robosoft.news.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.robosoft.news.applications.NewsApp
import com.robosoft.news.common.AppConstants
import com.robosoft.news.di.Component
import com.robosoft.news.network.ApiService
import com.robosoft.news.room.NewsDB
import com.robosoft.news.room.NewsTable
import com.robosoft.news.room.SearchTable
import com.robosoft.news.ui.home.models.Article
import com.robosoft.news.ui.home.models.NewsModels
import kotlinx.coroutines.*

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {
    val loading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()
    val objNewData = MutableLiveData<NewsModels?>()
    var job: Job? = null
    var database:NewsDB?=null
    var newTable:LiveData<NewsTable>?=null
    var searchDataSet = MutableLiveData<List<SearchTable>>()
    private var component = Component()

    companion object{
        fun initializeDB(context: Context) : NewsDB? {
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

    fun getTopHeadLine(country: String, apiKey: String) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = repository.getTopHeadLine(country, apiKey)
            if (response.isSuccessful) {
                val data = response.body()
                if (data?.status == "ok") {
                    val dataSet =
                        ApiService.getDataObject(data, NewsModels::class.qualifiedName.toString())
                                as NewsModels?
                    withContext(Dispatchers.Main) {
                        loading.value = false
                        objNewData.value = dataSet
                    }
                }
            } else {
                onError(response.message() ?: "")
            }
        }
    }

    fun getSearchData(q:String,apiKey: String){
        loading.value = true

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = repository.getSearchResult(q,"popularity" ,apiKey)
            if (response.isSuccessful) {
                val data = response.body()
                if (data?.status == "ok") {
                    val dataSet =
                        ApiService.getDataObject(data, NewsModels::class.qualifiedName.toString())
                                as NewsModels?
                    withContext(Dispatchers.Main) {
                        loading.value = false
                        objNewData.value = dataSet
                    }
                }
            } else {
                onError(response.message() ?: "")
            }
        }
    }

    fun addBookmark(context: Context,article:Article?){
        database = initializeDB(context)
        loading.value = true
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {

            val newTB = NewsTable(article?.author?:"",article?.content?:"",article?.description?:"",
            article?.publishedAt?:"",article?.title?:"",article?.url?:"",
            article?.urlToImage?:"",article?.source?.name?:"")
            database?.newsDAO()?.insertBookmark(newTB)
            withContext(Dispatchers.Main){
                AppConstants.showToast(context,"Bookmark successful")
                loading.value=false
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