package com.robosoft.news.ui.home.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.robosoft.news.R
import com.robosoft.news.applications.GlideApp
import com.robosoft.news.applications.NewsApp
import com.robosoft.news.interfaces.AddBookMark
import com.robosoft.news.ui.home.models.Article
import kotlinx.android.synthetic.main.row_news.view.*
import kotlinx.android.synthetic.main.single_textview.view.*

class HomeAdapter(private val context: Context,
                  private val dataList:ArrayList<Article>?,
                  private val addBookMark: AddBookMark,
                  private val onItemClick:((Article)->Unit))
    :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private val EMPTY = 1
    private val LISTDATA = 2

    fun updateData(arrayList: ArrayList<Article>, isRefresh: Boolean){
        if(isRefresh){
            dataList?.clear()
        }
        dataList?.addAll(arrayList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        return when (viewType) {
            EMPTY -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.single_textview, parent, false)
                view?.layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
                (view as TextView?)?.gravity = Gravity.CENTER
                view?.textSize = 22f
                //view?.setFont()
//                ((view as TextView).layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.CENTER_IN_PARENT)
                EmptyViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.row_news,
                    parent, false
                )
                NewsViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            EMPTY->{
                (holder as EmptyViewHolder).itemView.txt_names.text =
                    NewsApp.ctx.getString(R.string.empty_list, "news")
            }
            LISTDATA->{
                val views =(holder as NewsViewHolder?)?.itemView
                val dataSet = dataList?.get(position)
                views?.txtTitle?.text = dataSet?.title
                views?.txtDesc?.text = dataSet?.description
                views?.txtSource?.text = dataSet?.source?.name
                views?.imgNews?.let {
                    GlideApp.with(context).load(dataSet?.urlToImage)
                        .centerCrop().placeholder(R.drawable.ic_logo).error(R.drawable.ic_logo)
                        .into(it)
                }
                views?.imgBookMark?.setOnClickListener {
                    addBookMark.setBookMart(dataSet)
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return if(dataList?.isNullOrEmpty()==true)1 else dataList?.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(dataList?.isNullOrEmpty()==true) EMPTY else LISTDATA
    }

    inner class EmptyViewHolder(view: View?) : RecyclerView.ViewHolder(view!!)
    inner class NewsViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
            init {
                view?.setOnClickListener {
                    dataList?.get(adapterPosition)?.let { it1 -> onItemClick.invoke(it1) }
                }
            }
    }
}