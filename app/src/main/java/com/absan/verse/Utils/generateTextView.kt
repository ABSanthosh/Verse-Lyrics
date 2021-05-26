package com.absan.verse.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.absan.verse.R

@SuppressLint("SetTextI18n")
fun generateTextView(context: Context, text: String):TextView {
    val lyricLine = TextView(context)
    lyricLine.textAlignment = View.TEXT_ALIGNMENT_CENTER
    if (text == "") {
        lyricLine.text = "♫\n"
    } else {
        lyricLine.text = "${text} \n"
    }
    val params = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.setMargins(0, 0, 0, 15)
    lyricLine.layoutParams = params
    lyricLine.typeface = ResourcesCompat.getFont(context, R.font.walter_turncoat)
    lyricLine.textSize = 24F

    return lyricLine
}