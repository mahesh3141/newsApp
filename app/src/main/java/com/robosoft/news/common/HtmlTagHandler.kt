package com.robosoft.news.common

import android.text.Editable
import android.text.Html
import org.xml.sax.XMLReader

object HtmlTagHandler : Html.TagHandler {
    override fun handleTag(opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?) {
        if (tag == "hr")
            handleHrTag(opening, output)
    }

    private fun handleHrTag(opening: Boolean, output: Editable?) {
        output?.append("\n")
    }
}