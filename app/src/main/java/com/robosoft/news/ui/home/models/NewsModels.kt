package com.robosoft.news.ui.home.models
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
@Parcelize
 data class NewsModels(
    @SerializedName("articles")
    val articles: ArrayList<Article>?=null,
    @SerializedName("status")
    val status: String?=null,
    @SerializedName("totalResults")
    val totalResults: Int?=0
):Parcelable
@Parcelize
data class Article(
    @SerializedName("author")
    val author: String?=null,
    @SerializedName("content")
    val content: String?=null,
    @SerializedName("description")
    val description: String?=null,
    @SerializedName("publishedAt")
    val publishedAt: String?=null,
    @SerializedName("source")
    val source: Source?=null,
    @SerializedName("title")
    val title: String?=null,
    @SerializedName("url")
    val url: String?=null,
    @SerializedName("urlToImage")
    val urlToImage: String?=null
):Parcelable
@Parcelize
data class Source(
    @SerializedName("id")
    val id: String?=null,
    @SerializedName("name")
    val name: String?=null
):Parcelable