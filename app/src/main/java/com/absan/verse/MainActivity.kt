package com.absan.verse

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.absan.verse.helpers.*
import com.absan.verse.helpers.classes.Logger
import com.absan.verse.helpers.classes.Run
import com.absan.verse.helpers.classes.Spotify
import com.absan.verse.helpers.database.*
import com.absan.verse.helpers.data.*
import com.absan.verse.ui.*
import com.addisonelliott.segmentedbutton.SegmentedButton
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private var running = false
    private var isGoogle = true
    private val spotifyReceiverLyrics = Spotify.spotifyReceiver(::handleSongIntent)
    private var currentSong = Song()
    private var lastSong = Song()
    private var pausedSong = Song()
    private var networkCall = CoroutineScope(Dispatchers.Main)
    private var isSaved = false
    private val mainPrefInstance by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }
    private var prevTheme = "light"


    private val loggerServiceIntentForeground by lazy {
        Intent(
            "START_FOREGROUND",
            Uri.EMPTY,
            this,
            Logger::class.java
        )
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__main)

        // Navigation drawer - Start
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.navbar)

        updateSavedLyricsCount(this, drawerLayout)

        if (mainPrefInstance.getString("Theme", "light") == "dark") {
            drawerLayout.setScrimColor(
                ContextCompat.getColor(
                    this,
                    R.color.darkModeScrimColor
                )
            )
        }

        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.Navbar__Open,
            R.string.Navbar__Close
        )


        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        toolbar.setNavigationOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }

        findViewById<RelativeLayout>(R.id.openSavedLyrics).setOnClickListener {
            startActivity(Intent(this, SavedSongs::class.java))
        }

        findViewById<RelativeLayout>(R.id.openRecentSongs).setOnClickListener {
            startActivity(Intent(this, RecentlyPlayed::class.java))
        }

        findViewById<RelativeLayout>(R.id.openSettings).setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }

        val songName = findViewById<TextView>(R.id.songname)
        val themeToggle = findViewById<SegmentedButtonGroup>(R.id.navbar__themeSelector)
        val darkModeButton = findViewById<SegmentedButton>(R.id.navbar__darkMode)
        val autoModeButton = findViewById<SegmentedButton>(R.id.navbar__autoMode)
        val lightModeButton = findViewById<SegmentedButton>(R.id.navbar__lightMode)

        songName.isSelected = true

        when (mainPrefInstance.getString("Theme", "light")) {
            "dark" -> changeThemeIcon(
                darkModeButton,
                autoModeButton,
                lightModeButton,
                themeToggle,
                this, mainPrefInstance, 0, false
            )
            "default" -> changeThemeIcon(
                darkModeButton,
                autoModeButton,
                lightModeButton,
                themeToggle,
                this, mainPrefInstance, 1, false
            )
            "light" -> changeThemeIcon(
                darkModeButton,
                autoModeButton,
                lightModeButton,
                themeToggle,
                this, mainPrefInstance, 2, false
            )
        }

        darkModeButton.setOnClickListener {
            changeThemeIcon(
                darkModeButton,
                autoModeButton,
                lightModeButton,
                themeToggle,
                this, mainPrefInstance, 0
            )
            prevTheme = "dark"
        }
        autoModeButton.setOnClickListener {
            changeThemeIcon(
                darkModeButton,
                autoModeButton,
                lightModeButton,
                themeToggle,
                this, mainPrefInstance, 1
            )
            prevTheme = "default"
        }
        lightModeButton.setOnClickListener {
            changeThemeIcon(
                darkModeButton,
                autoModeButton,
                lightModeButton,
                themeToggle,
                this, mainPrefInstance, 2
            )
            prevTheme = "light"
        }

        //Navigation Drawer - End


        // Toggle for Ad mute function - Start
        val blockedAdCountTV = findViewById<TextView>(R.id.navbar__blockedAdCount)
        if (mainPrefInstance.getBoolean("MuteAd", false))
            blockedAdCountTV.text = mainPrefInstance.getInt("AdCount", 0).toString()
        else
            blockedAdCountTV.text = "--"
        // Toggle for Ad mute function - End

        // Save lyrics - Setup
        val bookmarkIcon = findViewById<ImageView>(R.id.bookmark)
        bookmarkIcon.setOnClickListener {
            if (isSaved) {
                removeSong(this, currentSong)
                isSaved = false
                setBookmark(isSaved, bookmarkIcon, this)
            } else {
                addSong(this, currentSong)
                isSaved = true
                setBookmark(isSaved, bookmarkIcon, this)
                // Log.e("SaveSong","$currentSong")
            }
            updateSavedLyricsCount(this, drawerLayout)
        }
        // Save lyrics - End
    }

    override fun onStart() {
        // update ad count - start
        val blockedAdCountTV = findViewById<TextView>(R.id.navbar__blockedAdCount)
        if (mainPrefInstance.getBoolean("MuteAd", false))
            blockedAdCountTV.text = mainPrefInstance.getInt("AdCount", 0).toString()
        else
            blockedAdCountTV.text = "--"
        // update ad count - End

        // update bookmark - start
        isSongSaved()
        val bookmarkIcon = findViewById<ImageView>(R.id.bookmark)
        if (isSaved) {
            setBookmark(isSaved, bookmarkIcon, this)
        } else {
            setBookmark(isSaved, bookmarkIcon, this)
        }
        // update bookmark - End

        ResetLyricView(
            findViewById(R.id.lyricsContainer),
            stuff = mainPrefInstance.getString("FontQuery", null).toString(),
            context = this
        )

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        drawerLayout.closeDrawer(GravityCompat.END)

        val themeToggle = findViewById<SegmentedButtonGroup>(R.id.navbar__themeSelector)
        val darkModeButton = findViewById<SegmentedButton>(R.id.navbar__darkMode)
        val autoModeButton = findViewById<SegmentedButton>(R.id.navbar__autoMode)
        val lightModeButton = findViewById<SegmentedButton>(R.id.navbar__lightMode)

        when (mainPrefInstance.getString("Theme", "light")) {
            "dark" -> {
                darkModeButton.removeDrawableTint()
                autoModeButton.drawableTint =
                    ContextCompat.getColor(this, R.color.themeToggleNonActiveTint)
                themeToggle.setPosition(0, false)
            }
            "default" -> {
                darkModeButton.drawableTint =
                    ContextCompat.getColor(this, R.color.themeToggleNonActiveTint)
                autoModeButton.removeDrawableTint()
                lightModeButton.drawableTint =
                    ContextCompat.getColor(this, R.color.themeToggleNonActiveTint)
                themeToggle.setPosition(1, false)
            }
            "light" -> {
                darkModeButton.drawableTint =
                    ContextCompat.getColor(this, R.color.themeToggleNonActiveTint)
                autoModeButton.drawableTint =
                    ContextCompat.getColor(this, R.color.themeToggleNonActiveTint)
                lightModeButton.removeDrawableTint()
                themeToggle.setPosition(2, false)
            }
        }

        startLoggerService()

        // First time Message - Start
        if (mainPrefInstance.getBoolean("FirstTime", true)) FirstTime().show(
            supportFragmentManager,
            "First time"
        )
        // First time Message - End

        super.onStart()
    }

    override fun onResume() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        updateSavedLyricsCount(this, drawerLayout)

        // update ad count - start
        val blockedAdCountTV = findViewById<TextView>(R.id.navbar__blockedAdCount)
        if (mainPrefInstance.getBoolean("MuteAd", false))
            blockedAdCountTV.text = mainPrefInstance.getInt("AdCount", 0).toString()
        else
            blockedAdCountTV.text = "--"
        // update ad count - End

        // update bookmark - start
        isSongSaved()
        val bookmarkIcon = findViewById<ImageView>(R.id.bookmark)
        if (isSaved) {
            setBookmark(isSaved, bookmarkIcon, this)
        } else {
            setBookmark(isSaved, bookmarkIcon, this)
        }
        // update bookmark - End

        super.onResume()
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)

        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        unregisterReceiver(spotifyReceiverLyrics)
        super.onDestroy()
    }

    private fun startLoggerService() {
        if (running) return
        registerReceiver(
            spotifyReceiverLyrics,
            Spotify.INTENT_FILTER
        )
        running = true
    }

    private fun isSongSaved(song: Song = currentSong) {
        isSaved = if (BookmarkDatabaseHandler(this).isAlreadySaved(song)) {
            setBookmark(
                true,
                findViewById<ImageView>(R.id.bookmark),
                this
            )
            true
        } else {
            setBookmark(
                false,
                findViewById<ImageView>(R.id.bookmark),
                this
            )
            false
        }
    }

    private fun handleSongIntent(song: Song) {
        // update ad count - Start
        val blockedAdCountTV = findViewById<TextView>(R.id.navbar__blockedAdCount)
        if (mainPrefInstance.getBoolean("MuteAd", false))
            blockedAdCountTV.text = mainPrefInstance.getInt("AdCount", 0).toString()
        else
            blockedAdCountTV.text = "--"
        // update ad count - End

        // Update bookmark - Start
        isSongSaved()
        // Update bookmark - End

        currentSong = song

        if (song.isDuplicateOf(lastSong)) return

        RecentlyPlayedDatabaseHandler(this).addRecentlyPlayed(song)

        lastSong = song

        //Show No Songs playing - Start
        //TODO: Fix UI
        when {
            song.playing -> {
//                if (findViewById<RelativeLayout>(R.id.NoSongParent).visibility == View.VISIBLE) {
//                    findViewById<RelativeLayout>(R.id.NoSongParent).visibility = View.GONE
//                }
                handleNewSongPlaying(song)
            }
            else -> {
//                if (findViewById<RelativeLayout>(R.id.NoSongParent).visibility == View.GONE) {
//                    findViewById<RelativeLayout>(R.id.NoSongParent).visibility = View.VISIBLE
//                }
                handleSongNotPlaying(song)
            }
        }
        //Show No Songs playing - End
    }

    private fun handleNewSongPlaying(newSong: Song) {
        val songName = findViewById<TextView>(R.id.songname)
        val artistName = findViewById<TextView>(R.id.artistName)

        songName.text = newSong.track
        artistName.text = newSong.artist

        RecentlyPlayedDatabaseHandler(this).addRecentlyPlayed(newSong)

        if (isGoogle) {
            googleLyrics(newSong)
        } else {
            musixmatchLyrics(newSong)
        }
    }

    private fun musixmatchLyrics(newSong: Song) {
        val tableLayout = findViewById<TableLayout>(R.id.lyricsContainer)
        if (newSong.id != pausedSong.id) {
            tableLayout.removeAllViews()
        }

        try {
            Run.handler.removeCallbacksAndMessages(null)
        } catch (err: Exception) {
        }
        networkCall.launch {
            MusixmatchSyncLyric(
                song = newSong,
                view = findViewById(R.id.lyricsContainer),
                parent = findViewById(R.id.LyricsView),
                context = applicationContext,
                activity = this@MainActivity
            )
        }
    }

    private fun googleLyrics(newSong: Song) {
        val tableLayout = findViewById<TableLayout>(R.id.lyricsContainer)
        if (newSong.id != pausedSong.id) {
            tableLayout.removeAllViews()
        }
        try {
            Run.handler.removeCallbacksAndMessages(null)
        } catch (err: Exception) {
        }
        networkCall.launch {
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

    private fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}