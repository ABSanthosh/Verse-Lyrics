package com.absan.verse.Helpers

import android.content.Context
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import com.absan.verse.R
import com.absan.verse.Helpers.Database.BookmarkDatabaseHandler

fun updateSavedLyricsCount(context:Context, drawerLayout:DrawerLayout){
    if(BookmarkDatabaseHandler(context).readLyrics().size == 0)
        drawerLayout.findViewById<TextView>(R.id.navbar__savedLyricsCount).text = "--"
    else
        drawerLayout.findViewById<TextView>(R.id.navbar__savedLyricsCount).text = BookmarkDatabaseHandler(context).readLyrics().size.toString()
}