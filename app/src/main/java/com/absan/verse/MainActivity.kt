package com.absan.verse

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.absan.verse.Utils.*
import com.absan.verse.data.*
import com.absan.verse.ui.*
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject


var count = 0

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toggle: ActionBarDrawerToggle
    private var running = false
    private val spotifyReceiverLyrics = Spotify.spotifyReceiver(::handleSongIntent)
    private var lastSong = Song()
    private var pausedSong = Song()
    private var NetworkCall = CoroutineScope(Dispatchers.Main)

    private val loggerServiceIntentForeground by lazy {
        Intent(
            "START_FOREGROUND",
            Uri.EMPTY,
            this,
            Logger::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set full screen
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__main)

        // Navigation drawer -- Start
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.navbar)
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.Navbar__Open,
            R.string.Navbar__Close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = true

        val navigationView = findViewById<NavigationView>(R.id.navView)
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.bringToFront()

        val logo: TextView = findViewById(R.id.logo)
        logo.visibility = View.VISIBLE
        //Navigation Drawer -- End

        // Toggle for Ad mute function - Start
        val menuItem: MenuItem = navigationView.menu.findItem(R.id.adblockmenu)
        val toggleButton: androidx.appcompat.widget.SwitchCompat =
            menuItem.actionView.findViewById(R.id.MuteAds__toggle)
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                this.startService(loggerServiceIntentForeground)
                Toast.makeText(
                    this,
                    "Muting service started",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                this.stopService(loggerServiceIntentForeground)
                Toast.makeText(
                    this,
                    "Muting service ended",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        // Toggle for Ad mute function - End
    }

    override fun onStart() {
        startLoggerService()
        super.onStart()
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun startLoggerService() {
        if (running) return
        registerReceiver(
            spotifyReceiverLyrics,
            Spotify.INTENT_FILTER
        )
        running = true
    }

    override fun onDestroy() {
        unregisterReceiver(spotifyReceiverLyrics)
        super.onDestroy()
    }

    private fun handleSongIntent(song: Song) {
        if (song.isDuplicateOf(lastSong)) return
        Log.e("FistSongIntent", "${song.id} ${song.playing}")
        lastSong = song
        when {
            song.playing -> {
                if (findViewById<TextView>(R.id.status).visibility == View.VISIBLE) {
                    findViewById<TextView>(R.id.status).visibility = View.GONE
                }
                handleNewSongPlaying(song)
            }
            else -> handleSongNotPlaying(song)
        }
    }

    private fun handleNewSongPlaying(newSong: Song) {
        val tableLayout = findViewById<TableLayout>(R.id.lyricsContainer)
        if (tableLayout.childCount != 0 && newSong.id != pausedSong.id) {
            tableLayout.removeAllViews()
        }
        val songName = findViewById<TextView>(R.id.songname)
        songName.text = newSong.track
        NetworkCall.launch {
            MusixmatchSyncLyric(
                song = newSong,
                view = findViewById(R.id.lyricsContainer),
                parent = findViewById(R.id.LyricsView),
                context = applicationContext,
                activity = this@MainActivity
            )
        }
//        NetworkCall.launch {
//            GoogleLyric(
//                albumName = newSong.album,
//                artistName = newSong.artist,
//                artistsName = newSong.artist,
//                songName = newSong.track,
//                songId = newSong.id,
//                view = findViewById(R.id.lyricsContainer),
//                context = applicationContext
//            )
//        }
    }

    private fun handleSongNotPlaying(song: Song) {
        pausedSong = song
        Run.handler.removeCallbacksAndMessages(null)
//        Log.e("SongNotPlaying", "Handle song not playing $song")
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.open_spotify -> OpenSpotify(this)
            R.id.rateapp -> RateOnPlayStore(this)
            R.id.otherapps -> OpenGitHub(this)
            R.id.feedback -> SendFeedback(this)

            R.id.adblockmenu -> {
                MuteAds().show(supportFragmentManager, "Ad Mute")
            }
            R.id.helper -> {
                HelpMe().show(supportFragmentManager, "Help me")
            }
            R.id.newfeatures -> {
                WhatsNew().show(supportFragmentManager, "Whats New")
            }
            R.id.aboutapp -> {
                About().show(supportFragmentManager, "About")
            }
            R.id.font -> {
                FontSelector().show(supportFragmentManager, "Font")
            }
            R.id.mode -> {
            }
            R.id.myname -> {
                count++;
                if (count >= 7) {
                    MyName(this)
                    count = 0
                }
            }
        }
        return true
    }
}