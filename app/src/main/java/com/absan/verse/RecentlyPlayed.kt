package com.absan.verse

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class RecentlyPlayed : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity__recentlyplayed)
        super.onCreate(savedInstanceState)


        findViewById<ImageView>(R.id.recentlyPlayedBackButton).setOnClickListener {
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