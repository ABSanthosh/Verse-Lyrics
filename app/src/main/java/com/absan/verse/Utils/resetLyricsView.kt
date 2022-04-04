package com.absan.verse.Utils

import android.content.Context
import android.graphics.Typeface
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.absan.verse.R
import com.absan.verse.data.Constants.FONTSIZE
import com.absan.verse.data.Constants.TYPEFACE


fun ResetLyricView(table: TableLayout, context: Context? = null) {
//    TODO: Add -1 to length to avoid licence if its there
    for (i in 0 until table.childCount) {
        if (table.getChildAt(i) != null) {
            (table.getChildAt(i) as TextView).setTypeface(
                if (TYPEFACE != Typeface.DEFAULT || context == null) TYPEFACE else (
                        ResourcesCompat.getFont(context, R.font.walter_turncoat)),
                Typeface.NORMAL
            )
            (table.getChildAt(i) as TextView).textSize = FONTSIZE
        }
    }
}