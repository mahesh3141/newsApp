package com.robosoft.news.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robosoft.news.applications.GlideApp
import com.robosoft.news.common.AppConstants
import com.robosoft.news.common.hide
import com.robosoft.news.common.show
import com.robosoft.news.databinding.ActivityHomeBinding
import com.robosoft.news.network.ApiService
import com.robosoft.news.ui.detail.DetailActivity
import com.robosoft.news.ui.home.adapters.HomeAdapter
import com.robosoft.news.interfaces.AddBookMark
import com.robosoft.news.ui.bookmark.BookMarkActivity
import com.robosoft.news.ui.home.models.Article
import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.widget.SearchView
import android.widget.CursorAdapter
import android.widget.SimpleCursorAdapter
import com.robosoft.news.R
import com.robosoft.news.common.hideKeyboard


class HomeActivity : AppCompatActivity(), AddBookMark {
    val consultant = ApiService.getApiClient()?.create(ApiService.NewsApi::class.java)
    val mainRepository = consultant?.let { HomeRepository(it) }
    val viewModel: HomeViewModel by viewModels {
        HomeFactory(mainRepository)
    }
    var binding: ActivityHomeBinding? = null
    var adapter: HomeAdapter? = null
    var totalCount = -1
    val pageLimit: Int = 10
    var isLoading = false
    var cursorAdapter: SimpleCursorAdapter? = null

    val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
            if (!isLoading) {
                if (linearLayoutManager != null && (totalCount) > pageLimit
                    && linearLayoutManager.findLastCompletelyVisibleItemPosition() >=
                    (recyclerView.adapter?.itemCount ?: 0) - (pageLimit * 0.5)
                ) {
                    loadPage((recyclerView.adapter?.itemCount ?: 0))
                }
            }
        }
    }
    lateinit var arrayNews: ArrayList<Article>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setHomeButtonEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        //supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_backarrow)
        supportActionBar?.title = ""
        supportActionBar?.setIcon(R.drawable.ic_logo)
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
        callTopHeadLine("in")
    }

    private fun callTopHeadLine(query: String) {

        /*Following code is for read the json from the assets and load the data
        * this code is just for testing
        * */
        //arrayNews = AppConstants.getDataFromAssets() ?: ArrayList()
        viewModel.getTopHeadLine(query, AppConstants.API_KEY)

        viewModel.objNewData.observe(this, { dataSet ->
            arrayNews = ArrayList()
            arrayNews = dataSet?.articles as ArrayList<Article>
            loadPage(0)
            /*Lazy Loading*/
            initScrollListener()
            loadFirstRecord(arrayNews)
        })

    }

    private fun loadFirstRecord(arrayNews: java.util.ArrayList<Article>) {
        arrayNews.first { data ->
            binding?.txtTopHeadLine?.text = data.title
            binding?.txtTopDesc?.text = data.description
            binding?.imgTopNews?.let {
                GlideApp.with(this).load(data.urlToImage)
                    .centerCrop().placeholder(R.drawable.ic_logo).error(R.drawable.ic_logo)
                    .into(it)
            }
            binding?.txtSource?.text = data.source?.name

            binding?.cslTopNews?.setOnClickListener {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putParcelableArrayListExtra("dataList", arrayNews)
                intent.putExtra("url", data.url)
                startActivity(intent)
            }
            binding?.imgBookMark?.setOnClickListener {
                setBookMart(data)
            }
            true
        }
    }

    private fun loadPage(startPos: Int) {
        if (!arrayNews.isNullOrEmpty()) {
            val data = arrayNews.subList(
                startPos,
                if ((arrayNews.size - 1) >= startPos + pageLimit) startPos + pageLimit else (arrayNews.size - 1)
            )
            loadMore(data)
        }
    }

    fun initScrollListener() {
        binding?.recycler?.addOnScrollListener(scrollListener)
    }


    private fun loadMore(arrayNews: MutableList<Article>) {
        if (totalCount == -1) {
            totalCount = arrayNews.size
        }
        if (adapter == null) {
            createRecycler(ArrayList(arrayNews))
        } else {
            arrayNews.let { adapter?.updateData(ArrayList(it), false) }
        }
    }

    private fun createRecycler(tempList: ArrayList<Article>?) {
        adapter = HomeAdapter(this, tempList, this) { data ->
            AppConstants.showToast(this, "Click Over " + data.title)
            val intent = Intent(this, DetailActivity::class.java)
            intent.putParcelableArrayListExtra("dataList", tempList)
            intent.putExtra("url", data.url)
            startActivity(intent)
        }
        binding?.recycler?.adapter = adapter
        adapter?.notifyDataSetChanged()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_book_search, menu)
        val searchMenu = menu.findItem(R.id.menu_search)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val search: SearchView = menu.findItem(R.id.menu_search)?.actionView as SearchView
        search.setSearchableInfo(manager.getSearchableInfo(componentName))
        search.queryHint = getString(R.string.search)
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.item_label)
        cursorAdapter = SimpleCursorAdapter(
            this, R.layout.search_item, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        search.suggestionsAdapter = cursorAdapter
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(value: String?): Boolean {
                viewModel.apply {
                    totalCount=-1
                    adapter = null
                    binding?.recycler?.adapter = null
                    binding?.recycler?.removeOnScrollListener(scrollListener)
                    addSearch(search.context, value)
                    getSearchData(value ?: "", AppConstants.API_KEY)
                }

                viewModel.objNewData.observe(this@HomeActivity, { dataSet ->
                    arrayNews = ArrayList()
                    arrayNews = dataSet?.articles as ArrayList<Article>
                    loadPage(0)
                    /*Lazy Loading*/
                    initScrollListener()
                    loadFirstRecord(arrayNews)
                })
                hideKeyboard(search)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                loadSearch(query)
                return true
            }

        })

        search.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            @SuppressLint("Range")
            override fun onSuggestionClick(position: Int): Boolean {
                hideKeyboard(search)
                val cursor = search.suggestionsAdapter.getItem(position) as Cursor
                val selection =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                search.setQuery(selection, false)
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun loadSearch(query: String?) {
        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
        query?.let { queryText ->
            if (queryText.length > 1) {
                viewModel.apply {
                    getAllSearch(this@HomeActivity)
                    searchDataSet.observe(this@HomeActivity, { dataSet ->
                        println("*** size " + dataSet.size)
                        dataSet?.forEachIndexed { index, searchTable ->
                            if (searchTable.title?.contains(queryText, true) == true) {
                                cursor.addRow(arrayOf(index, searchTable.title))
                            }
                        }
                    })
                }
            }
        }
        cursorAdapter?.changeCursor(cursor)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_bookmark -> {
                startActivity(Intent(this, BookMarkActivity::class.java))
            }
            R.id.menu_search -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    override fun setBookMart(article: Article?) {
        viewModel.addBookmark(this, article)
    }
}