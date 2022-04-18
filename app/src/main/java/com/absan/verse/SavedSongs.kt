package com.absan.verse

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.absan.verse.Utils.DatabaseRelated.DatabaseHandler
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
        if (DatabaseHandler(context).readLyrics().size > 0) {
            val recycler = findViewById<RecyclerView>(R.id.savedLyrics_Recycler)
            recycler.layoutManager = LinearLayoutManager(context)
            val itemAdapter = SaveLyric__ItemAdapter(context, DatabaseHandler(context).readLyrics())
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