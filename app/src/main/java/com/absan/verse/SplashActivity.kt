package com.absan.verse

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Switch
import android.widget.TextView
import com.absan.verse.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        lateinit var sp:SharedPreferences

        super.onCreate(savedInstanceState)
        sp = getSharedPreferences("theme", Context.MODE_PRIVATE)
/*
        if(sp.getBoolean("light",true)){
            setTheme(R.style.LightTheme);
        }else{
            setTheme(R.style.DarkTheme);
        }

 */
        if (sp.getString("light", "true")=="T") {
            setTheme(R.style.LightTheme);
        } else if(sp.getString("light", "true")=="F"){
            setTheme(R.style.DarkTheme);
        }else{
            if(isDarkThemeOn()){
                setTheme(R.style.DarkTheme);
            }else{
                setTheme(R.style.LightTheme);
            }
        }
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

        var editor: SharedPreferences.Editor = sharedPreferences.edit()
        blockad = false
        editor.putBoolean("blockad", blockad)
        editor.apply()

    }

    fun Context.isDarkThemeOn(): Boolean{
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}