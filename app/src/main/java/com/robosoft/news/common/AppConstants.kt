package com.robosoft.news.common

import android.app.AlertDialog
import android.content.Context
import android.icu.text.DateTimePatternGenerator.PatternInfo.OK
import android.text.Spanned
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robosoft.news.R
import com.robosoft.news.applications.NewsApp
import com.robosoft.news.ui.home.models.Article
import com.robosoft.news.ui.home.models.NewsModels
import okio.buffer
import okio.source
import java.io.IOException
import java.nio.charset.Charset
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object AppConstants {

    const val DOMAIN = "https://newsapi.org/"
    const val API_KEY:String= "88d4f091cea443c9b11ca230a09335d1"


    fun parseTDDMMYYYY(date: String): String {
        val incomingFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        val outgoingFormat = SimpleDateFormat("dd MMM,yyyy | HH:mm:ss", Locale.getDefault())
        return try {
            outgoingFormat.format(incomingFormat.parse(date))
        } catch (e: ParseException) {
            //e.printStackTrace()
            date
        }
    }

    fun parseTDDMM(date: String): String {
        val incomingFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        val outgoingFormat = SimpleDateFormat("dd MMM,yyyy", Locale.getDefault())
        return try {
            outgoingFormat.format(incomingFormat.parse(date))
        } catch (e: ParseException) {
            //e.printStackTrace()
            date
        }
    }

    fun showError(context: Context, errorMessage:String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage(errorMessage)
        builder.setPositiveButton(setHtmlText("<font color='#000000'>"+context.getString(R.string.OK)+"</font>")) { p0, p1 ->
            p0.dismiss()
        }
        val alert = builder.create()
        alert.setTitle(R.string.title_Error)
        alert.show()
    }

    fun setHtmlText(content: String): Spanned {
        var source = content
        source = source.replace("&lt;", "<").replace("&gt;", ">")

        return HtmlCompat.fromHtml(
            source, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
                    or HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH
                    or HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV
                    or HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE
                    or HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV
                    or HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_HEADING
                    or HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST
                    or HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS, null, HtmlTagHandler
        )
    }
    fun showToast(context:Context, strMsg:String){
        Toast.makeText(context,strMsg, Toast.LENGTH_LONG)?.show()
    }

    fun getDataFromAssets() :ArrayList<Article>?{
        val data = getJsonDataFromAsset(NewsApp.ctx, "TempData.json")
        val gson = Gson()
        val dataSet = gson.fromJson(data, NewsModels::class.java)
        val listNewsType = object : TypeToken<List<Article>>() {}.type
        println("*** Size "+dataSet?.articles?.size)
       // arrayTempData =Gson().fromJson(data,object :TypeToken<ArrayList<Article>>(){}.type) as ArrayList<Article>
        return dataSet?.articles
    }
    fun readFileFromAssets(context: Context, fileName: String): String {
        try {
            val input = context.assets.open(fileName)
//            val source = Okio.buffer(Okio.source(input))
            val source = input.source().buffer()
            return source.readByteString().string(Charset.forName("utf-8"))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }
}