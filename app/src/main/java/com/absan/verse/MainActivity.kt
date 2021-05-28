package com.absan.verse

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
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


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toggle: ActionBarDrawerToggle
    private var RickRollcount = 0
    private var running = false
    private var isGoogle = true
    private val spotifyReceiverLyrics = Spotify.spotifyReceiver(::handleSongIntent)
    private var lastSong = Song()
    private var pausedSong = Song()
    private var NetworkCall = CoroutineScope(Dispatchers.Main)
    private val mainPrefInstance by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }


    private val loggerServiceIntentForeground by lazy {
        Intent(
            "START_FOREGROUND",
            Uri.EMPTY,
            this,
            Logger::class.java
        )
    }

    companion object{

    }


    @SuppressLint("UseCompatLoadingForDrawables")
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

        // Sharedpref setup - start
        val sharedEditor = mainPrefInstance.edit()
        // Sharedpref setup - End


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

        //General setup - Start
        val lyricTableLayout:TableLayout = findViewById(R.id.lyricsContainer)
        //General setup - End



        // Toggle for Ad mute function - Start
        val menuItem__adblock: MenuItem = navigationView.menu.findItem(R.id.adblockmenu)
        val toggleButton__adblock: androidx.appcompat.widget.SwitchCompat =
            menuItem__adblock.actionView.findViewById(R.id.MuteAds__toggle)
        toggleButton__adblock.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                this.startService(loggerServiceIntentForeground)
                Toast.makeText(
                    this,
                    "Muting service started",
                    Toast.LENGTH_SHORT
                ).show()

                sharedEditor.apply {
                    putBoolean("MuteAd", true)
                }.apply()

            } else {
                this.stopService(loggerServiceIntentForeground)
                Toast.makeText(
                    this,
                    "Muting service ended",
                    Toast.LENGTH_SHORT
                ).show()
                sharedEditor.apply {
                    putBoolean("MuteAd", false)
                }.apply()
            }
        }
        // Toggle for Ad mute function - End

        // Toggle for Google and Musixmatch lyrics - Start
        val googleVmusixmatch: MenuItem = navigationView.menu.findItem(R.id.synclyricmenu)
        val tooglegoogleVmusixmatch: androidx.appcompat.widget.SwitchCompat =
            googleVmusixmatch.actionView.findViewById(R.id.SyncLyric__toggle)
        tooglegoogleVmusixmatch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(
                    this,
                    "Switched to Live Lyrics(Beta)",
                    Toast.LENGTH_SHORT
                ).show()
                isGoogle = false
                navigationView.menu.findItem(R.id.synclyricmenu).title =
                    getString(R.string.Navbar__SyncLyric)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    navigationView.menu.findItem(R.id.synclyricmenu).icon =
                        getDrawable(R.drawable.navbar__synclyric)
                }

                sharedEditor.apply {
                    putBoolean("SyncLyrics", true)
                }.apply()

                val tableLayout = findViewById<TableLayout>(R.id.lyricsContainer)
                tableLayout.removeAllViews()
                findViewById<TextView>(R.id.verseRestart).visibility = View.VISIBLE

            } else {
                Toast.makeText(
                    this,
                    "Switched to Normal Lyrics",
                    Toast.LENGTH_SHORT
                ).show()
                isGoogle = true
                navigationView.menu.findItem(R.id.synclyricmenu).title =
                    getString(R.string.Navbar__NormalLyric)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    navigationView.menu.findItem(R.id.synclyricmenu).icon =
                        getDrawable(R.drawable.navbar__normallyric)
                }

                sharedEditor.apply {
                    putBoolean("SyncLyrics", false)
                }.apply()

                Run.handler.removeCallbacksAndMessages(null)
                ResetLyricView(table = findViewById(R.id.lyricsContainer))
            }
        }
        // Toggle for Google and Musixmatch lyrics - End

    }

    override fun onStart() {
        startLoggerService()

        if (mainPrefInstance.getBoolean("FirstTime", true)) FirstTime().show(
            supportFragmentManager,
            "First time"
        )

        if (Constants.TYPEFACE == Typeface.DEFAULT) {
            val handlerThread = HandlerThread("fonts")
            handlerThread.start()
            requestCustomFont(
                this,
                mainPrefInstance.getString("FontQuery", null).toString(),
                Handler(handlerThread.looper)
            )
        }

        val navigationView = findViewById<NavigationView>(R.id.navView)
        navigationView.menu.findItem(R.id.adblockmenu)
            .actionView
            .findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.MuteAds__toggle)
            .isChecked = mainPrefInstance.getBoolean("MuteAd", false)

        navigationView.menu.findItem(R.id.synclyricmenu)
            .actionView
            .findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.SyncLyric__toggle)
            .isChecked = mainPrefInstance.getBoolean("SyncLyrics", false)

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
        val songName = findViewById<TextView>(R.id.songname)
        songName.text = newSong.track
        if (findViewById<TextView>(R.id.verseRestart).visibility == View.VISIBLE) {
            findViewById<TextView>(R.id.verseRestart).visibility = View.GONE
        }

        if (isGoogle) {
            GoogleLyrics(newSong)
        } else {
            MusixmatchLyrics(newSong)
        }

    }

    private fun MusixmatchLyrics(newSong: Song) {
        val tableLayout = findViewById<TableLayout>(R.id.lyricsContainer)
        if (newSong.id != pausedSong.id) {
            tableLayout.removeAllViews()
        }

        try {
            Run.handler.removeCallbacksAndMessages(null)
        } catch (err: Exception) {
        }
        NetworkCall.launch {
            MusixmatchSyncLyric(
                song = newSong,
                view = findViewById(R.id.lyricsContainer),
                parent = findViewById(R.id.LyricsView),
                context = applicationContext,
                activity = this@MainActivity
            )
        }
    }

    private fun GoogleLyrics(newSong: Song) {
        val tableLayout = findViewById<TableLayout>(R.id.lyricsContainer)
        if (newSong.id != pausedSong.id) {
            tableLayout.removeAllViews()
        }
        try {
            Run.handler.removeCallbacksAndMessages(null)
        } catch (err: Exception) {
        }
        NetworkCall.launch {
            GoogleLyric(
                song = newSong,
                view = findViewById(R.id.lyricsContainer),
                context = applicationContext
            )
        }
    }

    private fun handleSongNotPlaying(song: Song) {
        pausedSong = song
        Run.handler.removeCallbacksAndMessages(null)
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
//                HelpMe().show(supportFragmentManager, "Help me")
                ResetLyricView(table = findViewById(R.id.lyricsContainer))
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
                RickRollcount++;
                if (RickRollcount >= 7) {
                    MyName(this)
                    RickRollcount = 0
                }
            }
        }
        return true
    }
}