package com.absan.verse.helpers.objects

import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {
    private const val LIGHT_MODE = "light"
    private const val DARK_MODE = "dark"
    private const val DEFAULT_MODE = "default"


    fun applyTheme(themePref: String, isDarkThemeOn: Boolean = false) {
        when (themePref) {
            LIGHT_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            DARK_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            DEFAULT_MODE -> {
                if (isDarkThemeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

            }
        }
    }
}