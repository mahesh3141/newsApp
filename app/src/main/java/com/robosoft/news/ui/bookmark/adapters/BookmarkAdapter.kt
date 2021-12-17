package com.robosoft.news.ui.bookmark.adapters

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
import com.robosoft.news.common.AppConstants
import com.robosoft.news.interfaces.RemoveBookMark
import com.robosoft.news.room.NewsTable
import com.robosoft.news.ui.home.adapters.HomeAdapter
import com.robosoft.news.ui.home.models.Article
import kotlinx.android.synthetic.main.row_news.view.*
import kotlinx.android.synthetic.main.single_textview.view.*

class BookmarkAdapter(
    private val context: Context,
    private val dataList: ArrayList<NewsTable>?,
    private val removeBookMark: RemoveBookMark,
    private val onItemClick: ((NewsTable) -> Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val EMPTY = 1
    private val LISTDATA = 2


    fun updateBookmark(arrayList:ArrayList<NewsTable>){
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
                BookmarkViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            EMPTY -> {
                (holder as EmptyViewHolder).itemView.txt_names.text =
                    NewsApp.ctx.getString(R.string.empty_list, "bookmarks")
            }
            LISTDATA -> {
                val views = (holder as BookmarkViewHolder?)?.itemView
                val dataSet = dataList?.get(position)
                views?.txtTitle?.text = dataSet?.title
                views?.txtDesc?.text = dataSet?.description
                views?.txtSource?.text = dataSet?.sourceName
                views?.imgNews?.let {
                    GlideApp.with(context).load(dataSet?.urlToImage)
                        .centerCrop().placeholder(R.drawable.ic_logo).error(R.drawable.ic_logo)
                        .into(it)
                }
                views?.imgBookMark?.setOnClickListener {
                    removeBookMark.removeBookMark(dataSet?.Id)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (dataList?.isNullOrEmpty() == true) 1 else dataList?.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList?.isNullOrEmpty() == true) EMPTY else LISTDATA
    }

    inner class EmptyViewHolder(view: View?) : RecyclerView.ViewHolder(view!!)
    inner class BookmarkViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        init {
            view?.setOnClickListener {
                dataList?.get(adapterPosition)?.let { it1 -> onItemClick.invoke(it1) }
            }
        }
    }
}