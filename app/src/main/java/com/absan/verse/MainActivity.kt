package com.absan.verse

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.absan.verse.Utils.*
import com.absan.verse.Utils.DatabaseRelated.DatabaseHandler
import com.absan.verse.Utils.DatabaseRelated.addSong
import com.absan.verse.Utils.DatabaseRelated.removeSong
import com.absan.verse.Utils.DatabaseRelated.setBookmark
import com.absan.verse.data.*
import com.absan.verse.ui.*
import com.addisonelliott.segmentedbutton.SegmentedButton
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private var RickRollcount = 0
    private var running = false
    private var isGoogle = true
    private val spotifyReceiverLyrics = Spotify.spotifyReceiver(::handleSongIntent)
    private var currentSong = Song()
    private var lastSong = Song()
    private var pausedSong = Song()
    private var NetworkCall = CoroutineScope(Dispatchers.Main)
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

        // Sharedpref setup - start
        val sharedEditor = mainPrefInstance.edit()
        // Sharedpref setup - End


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
            );
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

        fun changeThemeIcon(position: Int, isAnimated: Boolean = true) {
            when (position) {
                0 -> {
                    // Darkmode
                    darkModeButton.drawable =
                        ContextCompat.getDrawable(this, R.drawable.navbar__moonfill)
                    darkModeButton.removeDrawableTint()

                    autoModeButton.drawable =
                        ContextCompat.getDrawable(this, R.drawable.navbar__autoframe)
                    autoModeButton.drawableTint =
                        ContextCompat.getColor(this, R.color.themeToggleNonActiveTint)

                    lightModeButton.drawable =
                        ContextCompat.getDrawable(this, R.drawable.navbar__sunframe)
                    lightModeButton.drawableTint =
                        ContextCompat.getColor(this, R.color.themeToggleNonActiveTint)

                    themeToggle.setPosition(0, isAnimated)
                    themeToggle.setOnPositionChangedListener {
                        ThemeHelper.applyTheme("dark")
                    }
                    mainPrefInstance.edit().apply { putString("Theme", "dark") }.apply()
                }
                1 -> {
                    // Automode
                    darkModeButton.drawable =
                        ContextCompat.getDrawable(this, R.drawable.navbar__moonframe)
                    darkModeButton.drawableTint =
                        ContextCompat.getColor(this, R.color.themeToggleNonActiveTint)

                    autoModeButton.drawable =
                        ContextCompat.getDrawable(this, R.drawable.navbar__autofill)
                    autoModeButton.removeDrawableTint()

                    lightModeButton.drawable =
                        ContextCompat.getDrawable(this, R.drawable.navbar__sunframe)
                    lightModeButton.drawableTint =
                        ContextCompat.getColor(this, R.color.themeToggleNonActiveTint)

                    themeToggle.setPosition(1, isAnimated)
                    themeToggle.setOnPositionChangedListener {

                    }
                    mainPrefInstance.edit().apply { putString("Theme", "default") }.apply()
                }
                2 -> {
                    // Lightmode
                    darkModeButton.drawable =
                        ContextCompat.getDrawable(this, R.drawable.navbar__moonframe)
                    darkModeButton.drawableTint =
                        ContextCompat.getColor(this, R.color.themeToggleNonActiveTint)

                    autoModeButton.drawable =
                        ContextCompat.getDrawable(this, R.drawable.navbar__autoframe)
                    autoModeButton.drawableTint =
                        ContextCompat.getColor(this, R.color.themeToggleNonActiveTint)

                    lightModeButton.drawable =
                        ContextCompat.getDrawable(this, R.drawable.navbar__sunfill)
                    lightModeButton.removeDrawableTint()

                    themeToggle.setPosition(2, isAnimated)
                    themeToggle.setOnPositionChangedListener {
                        ThemeHelper.applyTheme("light")
                    }
                    mainPrefInstance.edit().apply { putString("Theme", "light") }.apply()
                }
            }
        }

        when (mainPrefInstance.getString("Theme", "light")) {
            "dark" -> changeThemeIcon(0, false)
            "default" -> changeThemeIcon(1, false)
            "light" -> changeThemeIcon(2, false)
        }



        darkModeButton.setOnClickListener {
            changeThemeIcon(0)
            prevTheme = "dark"
        }
        autoModeButton.setOnClickListener {
            changeThemeIcon(1)
            prevTheme = "default"
        }
        lightModeButton.setOnClickListener {
            changeThemeIcon(2)
            prevTheme = "light"
        }

//        themeToggle.setOnPositionChangedListener {
//            Log.e("Log", themeToggle.position.toString())
//            changeThemeIcon(themeToggle.position)
//
//        }


//        val navigationView = findViewById<NavigationView>(R.id.navView)
//        navigationView.setNavigationItemSelectedListener(this)
//        navigationView.bringToFront()

//        val logo: TextView = findViewById(R.id.logo)
//        logo.visibility = View.VISIBLE
        //Navigation Drawer - End


        // Toggle for Ad mute function - Start
        val blockedAdCountTV = findViewById<TextView>(R.id.navbar__blockedAdCount)
        if (mainPrefInstance.getBoolean("MuteAd", false))
            blockedAdCountTV.text = mainPrefInstance.getInt("AdCount", 0).toString()
        else
            blockedAdCountTV.text = "--"
        // Toggle for Ad mute function - End

        // Toggle for Google and Musixmatch lyrics - Start
//        val googleVmusixmatch: MenuItem = navigationView.menu.findItem(R.id.synclyricmenu)
//        val tooglegoogleVmusixmatch: androidx.appcompat.widget.SwitchCompat =
//            googleVmusixmatch.actionView.findViewById(R.id.SyncLyric__toggle)
//        tooglegoogleVmusixmatch.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                Toast.makeText(
//                    this,
//                    "Switched to Live Lyrics(Beta)",
//                    Toast.LENGTH_SHORT
//                ).show()
//                isGoogle = false
//                navigationView.menu.findItem(R.id.synclyricmenu).title =
//                    getString(R.string.Navbar__SyncLyric)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    navigationView.menu.findItem(R.id.synclyricmenu).icon =
//                        getDrawable(R.drawable.navbar__synclyric)
//                }
//
//                sharedEditor.apply {
//                    putBoolean("SyncLyrics", true)
//                }.apply()
//
//                val tableLayout = findViewById<TableLayout>(R.id.lyricsContainer)
//                tableLayout.removeAllViews()
//                findViewById<TextView>(R.id.verseRestart).visibility = View.VISIBLE
//
//            } else {
//                Toast.makeText(
//                    this,
//                    "Switched to Normal Lyrics",
//                    Toast.LENGTH_SHORT
//                ).show()
//                isGoogle = true
//                navigationView.menu.findItem(R.id.synclyricmenu).title =
//                    getString(R.string.Navbar__NormalLyric)
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    navigationView.menu.findItem(R.id.synclyricmenu).icon =
//                        getDrawable(R.drawable.navbar__normallyric)
//                }
//
//                sharedEditor.apply {
//                    putBoolean("SyncLyrics", false)
//                }.apply()
//
//                Run.handler.removeCallbacksAndMessages(null)
//                ResetLyricView(table = findViewById(R.id.lyricsContainer))
//            }
//        }
        // Toggle for Google and Musixmatch lyrics - End

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

    private fun isSongSaved(song: Song = currentSong) {
        isSaved = if (DatabaseHandler(this).isAlreadySaved(song)) {
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

        if (mainPrefInstance.getBoolean("FirstTime", true)) FirstTime().show(
            supportFragmentManager,
            "First time"
        )

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
        // update ad count - Start
        val blockedAdCountTV = findViewById<TextView>(R.id.navbar__blockedAdCount)
        if (mainPrefInstance.getBoolean("MuteAd", false))
            blockedAdCountTV.text = mainPrefInstance.getInt("AdCount", 0).toString()
        else
            blockedAdCountTV.text = "--"
        // update ad count - End

        isSongSaved()
        currentSong = song

        if (song.isDuplicateOf(lastSong)) return

        // Set bookmark if it already exists in db


        lastSong = song
        when {
            song.playing -> {
                if (findViewById<RelativeLayout>(R.id.NoSongParent).visibility == View.VISIBLE) {
                    findViewById<RelativeLayout>(R.id.NoSongParent).visibility = View.GONE
                }
                handleNewSongPlaying(song)
            }
            else -> {
                if (findViewById<RelativeLayout>(R.id.NoSongParent).visibility == View.GONE) {
                    findViewById<RelativeLayout>(R.id.NoSongParent).visibility = View.VISIBLE
                }
                handleSongNotPlaying(song)
            }
        }
    }

    private fun handleNewSongPlaying(newSong: Song) {
        val songName = findViewById<TextView>(R.id.songname)
        val artistName = findViewById<TextView>(R.id.artistName)

        songName.text = newSong.track
        artistName.text = newSong.artist
//        if (findViewById<TextView>(R.id.verseRestart).visibility == View.VISIBLE) {
//            findViewById<TextView>(R.id.verseRestart).visibility = View.GONE
//        }

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
//        if (findViewById<TextView>(R.id.verseRestart).visibility == View.VISIBLE) {
//            findViewById<TextView>(R.id.verseRestart).visibility = View.GONE
//        }
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
//        if (findViewById<TextView>(R.id.verseRestart).visibility == View.VISIBLE) {
//            findViewById<TextView>(R.id.verseRestart).visibility = View.GONE
//        }
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
//        if (findViewById<RelativeLayout>(R.id.NoSongParent).visibility == View.VISIBLE) {
//            findViewById<RelativeLayout>(R.id.NoSongParent).visibility = View.GONE
//        } else {
//            findViewById<RelativeLayout>(R.id.NoSongParent).visibility = View.VISIBLE
//        }
        Run.handler.removeCallbacksAndMessages(null)
    }

    fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}