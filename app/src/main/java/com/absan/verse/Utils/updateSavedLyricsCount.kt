package com.absan.verse.Utils

import android.content.Context
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import com.absan.verse.R
import com.absan.verse.Utils.DatabaseRelated.DatabaseHandler

fun updateSavedLyricsCount(context:Context, drawerLayout:DrawerLayout){
    if(DatabaseHandler(context).readLyrics().size == 0)
        drawerLayout.findViewById<TextView>(R.id.navbar__savedLyricsCount).text = "--"
    else
        drawerLayout.findViewById<TextView>(R.id.navbar__savedLyricsCount).text = DatabaseHandler(context).readLyrics().size.toString()
}