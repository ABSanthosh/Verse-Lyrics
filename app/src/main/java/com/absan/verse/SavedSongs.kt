package com.absan.verse

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.absan.verse.Utils.DatabaseRelated.BookmarkDatabaseHandler
import com.absan.verse.data.SaveLyric__ItemAdapter

class SavedSongs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity__savedsongs)
        super.onCreate(savedInstanceState)

        findViewById<ImageView>(R.id.savedSongBackButton).setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        setAdapter(this)
        super.onStart()
    }

    private fun setAdapter(context: Context) {
        val savedSongLen = BookmarkDatabaseHandler(context).readLyrics().size
        val messageText = findViewById<TextView>(R.id.saveLyrics__emptyMessage)
        if (savedSongLen == 0) {
            messageText.visibility = View.VISIBLE
        } else {
            messageText.visibility = View.GONE
        }

        if (savedSongLen > 0) {
            val recycler = findViewById<RecyclerView>(R.id.savedLyrics_Recycler)
            recycler.layoutManager = LinearLayoutManager(context)
            val itemAdapter =
                SaveLyric__ItemAdapter(context, BookmarkDatabaseHandler(context).readLyrics(), recycler, messageText)
            recycler.adapter = itemAdapter
        }
    }

    override fun onBackPressed() {
        try {
            finish();
        } catch (err: Exception) {
            super.onBackPressed()
        }
    }
}