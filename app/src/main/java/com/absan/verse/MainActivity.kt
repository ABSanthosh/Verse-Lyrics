package com.absan.verse

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.absan.verse.Utils.*
import com.absan.verse.ui.*
import com.google.android.material.navigation.NavigationView


var count = 0

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toggle: ActionBarDrawerToggle;

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
        setContentView(R.layout.activity_main)

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

        val NavigationView = findViewById<NavigationView>(R.id.navView)
        NavigationView.setNavigationItemSelectedListener(this)
        NavigationView.bringToFront()

        val logo: TextView = findViewById(R.id.logo)
        logo.visibility = View.VISIBLE
        //Navigation Drawer -- End

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