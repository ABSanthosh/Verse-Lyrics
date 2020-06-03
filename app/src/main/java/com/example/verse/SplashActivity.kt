package com.example.verse

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lateinit var logomovement: Animation
        logomovement = AnimationUtils.loadAnimation(this, R.anim.logomovement)

        var sharedPreferences:SharedPreferences = getSharedPreferences("Launchtime", Context.MODE_PRIVATE)

        Handler().postDelayed({
            val slogo: TextView = findViewById(R.id.splashlogo)
            slogo.startAnimation(logomovement)


            Handler().postDelayed({ slogo.gravity = Gravity.TOP }, 1500)
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.splashfadin, R.anim.splashfadeout);
            }, 1750)
        }, 2000)
    }
}