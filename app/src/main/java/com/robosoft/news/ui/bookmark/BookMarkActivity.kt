package com.robosoft.news.ui.bookmark

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.MenuItem
import android.widget.CursorAdapter
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robosoft.news.R
import com.robosoft.news.common.AppConstants
import com.robosoft.news.common.hide
import com.robosoft.news.common.hideKeyboard
import com.robosoft.news.common.show
import com.robosoft.news.databinding.ActivityBookMarkBinding
import com.robosoft.news.interfaces.RemoveBookMark
import com.robosoft.news.room.NewsTable
import com.robosoft.news.ui.bookmark.adapters.BookmarkAdapter

class BookMarkActivity : AppCompatActivity(),RemoveBookMark {
    var binding:ActivityBookMarkBinding?=null
    val mainRepository = BookMarkRepository()
    val viewModel:BookMarkViewModel by viewModels{
        BookMarkFactory(mainRepository)
    }
    var bookmarkList: ArrayList<NewsTable>?=null
    var adapter : BookmarkAdapter?=null
    var cursorAdapter : SimpleCursorAdapter?=null
    val pageLimit: Int = 5
    var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookMarkBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_backarrow)
        supportActionBar?.title = getString(R.string.bookmarkNews)
        viewModel.loading.observe(this, {
            if (it) {
                binding?.progressDialog?.show()
                binding?.cslLoader?.show()
            } else {
                binding?.progressDialog?.hide()
                binding?.cslLoader?.hide()
            }
        })
        viewModel.errorMessage.observe(this, {
            AppConstants.showError(this, it ?: "")
        })
        loadBookMarks()
    }

    private fun loadBookMarks() {
        viewModel.newDataSet?.observe(this,{data->
            println("***==> "+data?.size)
            bookmarkList = data as ArrayList<NewsTable>?
           // displayBookmark()
            loadPage(0)
            initScrollListener(bookmarkList)
        })
        viewModel.getAllBookmarks(this)

    }

    private fun initScrollListener(bookmarkList: java.util.ArrayList<NewsTable>?) {
        binding?.recycler?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && (recyclerView.adapter?.itemCount
                            ?: 0) >= pageLimit && linearLayoutManager.findLastCompletelyVisibleItemPosition() >=
                        (recyclerView.adapter?.itemCount ?: 0) - (pageLimit * 0.5)
                    ) {
                        loadPage((recyclerView.adapter?.itemCount ?: 0))
                    }
                } else {
                    println("**** xxx ")
                }

            }
        })
    }

    private fun loadPage(startPos: Int) {
        if(!bookmarkList.isNullOrEmpty()){
            val data = bookmarkList?.subList(
                startPos,
                if((bookmarkList?.size?:0-1)>=startPos+pageLimit) startPos + pageLimit else (bookmarkList?.size?:0-1)
            )
            loadMore(data)
        }
    }
    var totalCount = -1
    private fun loadMore(arrayList: MutableList<NewsTable>?) {
        if(totalCount==-1){
            totalCount = arrayList?.size?:0
        }
        if(adapter==null){
            displayBookmark(ArrayList(arrayList))
        }else{
            arrayList?.let { adapter?.updateBookmark(ArrayList(it)) }
        }
    }

    private fun displayBookmark(arrayList: ArrayList<NewsTable>?) {
        adapter = BookmarkAdapter(this, arrayList,this){

        }
        binding?.recycler?.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_book_search, menu)
        menu.findItem(R.id.menu_bookmark)?.hide()
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val search: SearchView = menu.findItem(R.id.menu_search)?.actionView as SearchView
        search.setSearchableInfo(manager.getSearchableInfo(componentName))
        search.queryHint = getString(R.string.search)
//        search.findViewById<AutoCompleteTextView>(R.id.search_src_text).threshold = 1

        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.item_label)
        cursorAdapter = SimpleCursorAdapter(this
            , R.layout.search_item, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)

        search.suggestionsAdapter = cursorAdapter
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(value: String?): Boolean {
              //  callTopHeadLine(value?:"")
                viewModel.addSearch(search.context, value)
                hideKeyboard(search)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                loadSearch(query)
                return true
            }

        })

        search.setOnSuggestionListener(object : SearchView.OnSuggestionListener{
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            @SuppressLint("Range")
            override fun onSuggestionClick(position: Int): Boolean {
                hideKeyboard(search)
                val cursor = search.suggestionsAdapter.getItem(position) as Cursor
                val selection = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                search.setQuery(selection, false)
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }
    private fun loadSearch(query: String?) {
        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
        query?.let { queryText->
            if (queryText.length > 1) {
                viewModel.getAllSearch(this)
                viewModel.searchDataSet.observe(this, { dataSet ->
                    println("*** size "+dataSet.size)
                    dataSet?.forEachIndexed { index, searchTable ->
                        println("*** valsearch  "+searchTable.title)
                        if(searchTable.title?.contains(queryText,true)==true){
                            cursor.addRow(arrayOf(index,searchTable.title))
                        }
                    }
                })
            }
        }
        cursorAdapter?.changeCursor(cursor)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            /* R.id.search -> Toast.makeText(this, "Search Clicked", Toast.LENGTH_SHORT).show()
             R.id.refresh -> Toast.makeText(this, "Refresh Clicked", Toast.LENGTH_SHORT).show()
             R.id.copy -> Toast.makeText(this, "Copy Clicked", Toast.LENGTH_SHORT).show()*/
            android.R.id.home -> {
                onBackPressed()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        binding=null
        super.onDestroy()
    }

    override fun removeBookMark(id: Int?) {
        viewModel.removeBookMark(this,id)
        viewModel.deleteRecord.observe(this,{data->
           // viewModel.errorMessage.value = data
            if(data=="success"){
                AppConstants.showToast(this,"Deleted successful")
                adapter=null
                loadBookMarks()
            }
        })

    }

}