package com.absan.verse.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.absan.verse.R
import com.absan.verse.helpers.objects.Constants.TYPEFACE

@SuppressLint("SetTextI18n")
fun generateTextView(context: Context, text: String): TextView {
    val mainPrefInstance by lazy { context.getSharedPreferences("main", Context.MODE_PRIVATE) }

    val lyricLine = TextView(context)
    lyricLine.textAlignment = View.TEXT_ALIGNMENT_CENTER
//    lyricLine.setPadding(6, 0, 6, 0)
    lyricLine.gravity = Gravity.CENTER
    lyricLine.ellipsize = TextUtils.TruncateAt.END


    if (mainPrefInstance.getString("Theme", "light") == "light") {
        lyricLine.setTextColor(
            Color.parseColor("#55524B")
        )
    } else if (mainPrefInstance.getString("Theme", "light") == "default") {
        lyricLine.setTextColor(
            Color.parseColor("#55524B")
        )
    } else {
        lyricLine.setTextColor(
            Color.parseColor("#CCC1A5")
        )
    }

    if (text == "") {
        lyricLine.text = "♫\n"
    } else {
        lyricLine.text = "$text \n"
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

    if (mainPrefInstance.getFloat("FontSize", 0f) != 0f) {
        lyricLine.textSize = mainPrefInstance.getFloat("FontSize", 0f)
    } else {
        lyricLine.textSize = 24F
    }


    return lyricLine
}