package com.absan.verse.helpers.database

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat.getDrawable
import com.absan.verse.R

fun setBookmark(isSaved: Boolean, bookmarkIcon: ImageView, context: Context) {
    if (isSaved) {
        bookmarkIcon.setImageDrawable(getDrawable(context, R.drawable.main__bookmark_selected))
    } else {
        bookmarkIcon.setImageDrawable(getDrawable(context, R.drawable.main__bookmark))
    }
}