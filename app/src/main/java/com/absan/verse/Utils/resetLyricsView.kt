package com.absan.verse.Utils

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.absan.verse.R
import com.absan.verse.data.Constants.TYPEFACE


fun ResetLyricView(table: TableLayout, context: Context? = null) {
    for(i in 0 until table.childCount-1) {
        if (table.getChildAt(i) != null) {
            (table.getChildAt(i) as TextView).setTypeface(
                if(TYPEFACE != Typeface.DEFAULT || context == null) TYPEFACE else (
                    ResourcesCompat.getFont(context,R.font.walter_turncoat)),
                Typeface.NORMAL
            )
        }
    }
}