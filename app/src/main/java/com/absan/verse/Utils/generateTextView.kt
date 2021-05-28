package com.absan.verse.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.absan.verse.R
import com.absan.verse.data.Constants.TYPEFACE

@SuppressLint("SetTextI18n")
fun generateTextView(context: Context, text: String): TextView {
    val mainPrefInstance by lazy { context.getSharedPreferences("main", Context.MODE_PRIVATE) }

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
    if (mainPrefInstance.getString("FontQuery", null) != null) {
        if (TYPEFACE != Typeface.DEFAULT) {
            lyricLine.typeface = TYPEFACE
        }
    } else {
        lyricLine.typeface = ResourcesCompat.getFont(context, R.font.walter_turncoat)
    }
    lyricLine.textSize = 24F

    return lyricLine
}