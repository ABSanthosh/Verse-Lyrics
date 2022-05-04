package com.absan.verse

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.absan.verse.Utils.DatabaseRelated.BookmarkDatabaseHandler
import com.absan.verse.Utils.DatabaseRelated.RecentlyPlayedDatabaseHandler
import com.absan.verse.data.RecentlyPlayed__ComponentAdapter
import com.absan.verse.data.SaveLyric__ItemAdapter
import com.absan.verse.data.Song

class RecentlyPlayed : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity__recentlyplayed)
        super.onCreate(savedInstanceState)

        findViewById<ImageView>(R.id.recentlyPlayedBackButton).setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        setComponentAdapter(this)
        super.onStart()
    }

    private fun setComponentAdapter(context: Context) {
        val databaseData = RecentlyPlayedDatabaseHandler(this).readRecentlyPlayed()
        val historyLen = databaseData.size

        val messageText = findViewById<TextView>(R.id.recentlyPlayed__emptyMessage)
        if (historyLen == 0) {
            messageText.visibility = View.VISIBLE
        } else {
            messageText.visibility = View.GONE
        }

        if (historyLen > 0) {
            val recycler = findViewById<RecyclerView>(R.id.recentlyPlayed__componentHolder)
            recycler.layoutManager = LinearLayoutManager(context)
            val componentAdapter =
                RecentlyPlayed__ComponentAdapter(context, databaseData)
            recycler.adapter = componentAdapter
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