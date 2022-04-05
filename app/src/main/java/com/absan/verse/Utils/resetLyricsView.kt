package com.absan.verse.Utils

import android.content.Context
import android.graphics.Typeface
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.absan.verse.R
import com.absan.verse.data.Constants.FONTSIZE
import com.absan.verse.data.Constants.TYPEFACE


fun ResetLyricView(table: TableLayout, context: Context, stuff: String = "def") {
//    TODO: Add -1 to length to avoid licence if its there
    for (i in 0 until table.childCount) {
        val lyricsLine = (table.getChildAt(i) as TextView)
        if (table.getChildAt(i) != null) {
            lyricsLine.typeface =
                if (TYPEFACE != Typeface.DEFAULT) TYPEFACE else (
                        ResourcesCompat.getFont(context, R.font.walter_turncoat))
            lyricsLine.textSize = FONTSIZE
        }
    }
}