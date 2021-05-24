package com.absan.verse

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.absan.verse.Utils.*
import com.absan.verse.ui.*
import com.google.android.material.navigation.NavigationView


var count = 0

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toggle: ActionBarDrawerToggle
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

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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