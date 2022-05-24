package com.absan.verse

import android.app.Application
import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.HandlerThread
import com.absan.verse.helpers.objects.ThemeHelper
import com.absan.verse.helpers.requestCustomFont
import com.absan.verse.helpers.objects.Constants

class DarkThemeApplication : Application() {
    private val mainPrefInstance by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }
    override fun onCreate() {
        super.onCreate()
        val themePref = mainPrefInstance.getString("Theme", ThemeHelper.DEFAULT_MODE)!!
        ThemeHelper.applyTheme(themePref)

        if (Constants.TYPEFACE == Typeface.DEFAULT ||
            mainPrefInstance.getString("FontQuery", null) != null
        ) {
            val handlerThread = HandlerThread("fonts")
            handlerThread.start()
            requestCustomFont(
                this,
                mainPrefInstance.getString("FontQuery", null).toString(),
                Handler(handlerThread.looper)
            )
        }
    }
}
