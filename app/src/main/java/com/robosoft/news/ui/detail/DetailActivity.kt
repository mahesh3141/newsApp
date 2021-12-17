package com.robosoft.news.ui.detail

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.robosoft.news.R
import com.robosoft.news.common.AppConstants
import com.robosoft.news.common.hide
import com.robosoft.news.common.show
import com.robosoft.news.databinding.ActivityDetailBinding
import com.robosoft.news.ui.home.adapters.HomeAdapter
import com.robosoft.news.interfaces.AddBookMark
import com.robosoft.news.ui.home.models.Article

class DetailActivity : AppCompatActivity(), AddBookMark {

    var binding:ActivityDetailBinding?=null

    var arrayList:ArrayList<Article>?=null
    var webUrl:String?=null
    var adapter: HomeAdapter? = null
    val tempArray = ArrayList<Article>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.icon_backarrow)

        arrayList= intent.extras?.getParcelableArrayList<Article>("dataList")
        webUrl = intent.extras?.getString("url")
        supportActionBar?.title = webUrl

        loadWebView(webUrl)
        loadRecycler(arrayList)
    }

    private fun loadRecycler(arrayList: java.util.ArrayList<Article>?) {

        for (i in 0 until 2){
            arrayList?.get(i)?.let { tempArray.add(it) }
        }
        adapter = HomeAdapter(this, tempArray,this) { data ->
            AppConstants.showToast(this, data?.title?:"")
            loadWebView(data?.url)
        }
        binding?.recycler?.hasFixedSize()
        binding?.recycler?.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

    private fun loadWebView(webUrl: String?) {
        binding?.webView?.settings?.javaScriptEnabled = true
        binding?.webView?.settings?.domStorageEnabled = true
        binding?.webView?.isVerticalScrollBarEnabled = true
        binding?.webView?.isHorizontalScrollBarEnabled = true
        binding?.webView?.clearHistory()
        binding?.webView?.webChromeClient = WebChromeClient()
        binding?.webView?.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding?.cslLoader?.show()
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding?.cslLoader?.hide()
            }
        }
        binding?.webView?.loadUrl(webUrl ?: "")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setBookMart(article:Article?) {
        println("*** data "+  article?.title)
    }
}