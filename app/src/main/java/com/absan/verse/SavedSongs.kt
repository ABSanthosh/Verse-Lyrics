package com.absan.verse

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SavedSongs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity__savedsongs)
        super.onCreate(savedInstanceState)


        findViewById<ImageView>(R.id.savedSongBackButton).setOnClickListener {
            finish()
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