package com.absan.verse

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat


class SplashScreen : AppCompatActivity() {

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

        // Set activity layout
        setContentView(R.layout.splashscreen)

        // Set custom font
        val SplashLogo: TextView = findViewById(R.id.splashlogo)
        SplashLogo.typeface = ResourcesCompat.getFont(this, R.font.bungee_shade)

        // Load Animation
        val LogoAnimation: Animation = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.logoanimation
        )
        LogoAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                SplashLogo.gravity = Gravity.TOP
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.splashfadin, R.anim.splashfadeout);
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })

        Handler(Looper.getMainLooper()).postDelayed({
            // Start animation
            SplashLogo.startAnimation(LogoAnimation)
        }, 2000)
        super.onCreate(savedInstanceState)


    }

}