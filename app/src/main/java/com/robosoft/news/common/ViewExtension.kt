package com.robosoft.news.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide(isInvisible: Boolean = false) {
    visibility = if (isInvisible) View.INVISIBLE else View.GONE
}

fun MenuItem.show() {
    isVisible = true
}

fun MenuItem.hide() {
    isVisible = false
}
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun TextView.setHtmlText(text: String?, @SuppressLint("InlinedApi") flags: Int = Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM or Html.FROM_HTML_MODE_COMPACT or Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH or Html.FROM_HTML_SEPARATOR_LINE_BREAK_DIV) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        setText(Html.fromHtml(text, flags))
    } else
        setText(Html.fromHtml(text))
}


fun TextView.drawableTintColor(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        compoundDrawableTintList = ColorStateList.valueOf(color)
    } else {
        compoundDrawablesRelative.forEach {
            it?.setTint(color)
        }
    }
}