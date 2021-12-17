package com.robosoft.news.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class NewsTable(
    @ColumnInfo(name="author")
    var author:String?=null,
    @ColumnInfo(name="content")
    var content:String?=null,
    @ColumnInfo(name="description")
    var description:String?=null,
    @ColumnInfo(name="publishedAt")
    var publishedAt:String?=null,
    @ColumnInfo(name="title")
    var title:String?=null,
    @ColumnInfo(name="url")
    var url:String?=null,
    @ColumnInfo(name="urlToImage")
    var urlToImage:String?=null,
    @ColumnInfo(name="source_name")
    var sourceName:String?=null,
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var Id: Int? = null
}