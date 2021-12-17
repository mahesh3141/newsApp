package com.robosoft.news.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.robosoft.news.common.AppConstants
import com.robosoft.news.ui.home.models.NewsModels
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File
import java.util.concurrent.TimeUnit

object ApiService {
    private var retrofit: Retrofit?=null
    private val params = HashMap<String, String>()
    private const val BASE_URL = AppConstants.DOMAIN

    init {
        val interceptor = HttpLoggingInterceptor()
        /* if(BuildConfig.DEBUG)*/
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor)
            .connectTimeout(2L, TimeUnit.MINUTES)
            .readTimeout(2L, TimeUnit.MINUTES)
            .writeTimeout(2L, TimeUnit.MINUTES)
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .create()
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client) // getUnsafeOkHttpClient()
            .build()
    }

    fun getDataObject(data: Any, modelName: String): Any {
        val gson = Gson()
        val tmpData = gson.toJson(data)
        val model = Class.forName(modelName)
        return gson.fromJson(tmpData, model)
    }

    fun getApiClient(): Retrofit? {
        return retrofit?.newBuilder()?.baseUrl(AppConstants.DOMAIN)?.build()
    }
    private fun getRequestBody(file: File, contentType: String): RequestBody {
//        return RequestBody.create(MediaType.parse(contentType), file)
        return RequestBody.create(contentType.toMediaTypeOrNull(), file)
    }


    interface NewsApi{
        @GET("v2/top-headlines")
        suspend fun getTopHeadLine(@Query("country") name: String?,
                                   @Query("apiKey") apiKey: String?): Response<NewsModels>

        @GET("V2/everything")
        suspend fun getEverything(@Query("q") q:String?,
                                  @Query("sortBy")sortBy:String?,
                                  @Query("apiKey") apiKey: String?): Response<NewsModels>
    }


}