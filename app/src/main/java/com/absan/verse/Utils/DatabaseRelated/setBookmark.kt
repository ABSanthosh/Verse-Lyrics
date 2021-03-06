package com.absan.verse.Utils.DatabaseRelated

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