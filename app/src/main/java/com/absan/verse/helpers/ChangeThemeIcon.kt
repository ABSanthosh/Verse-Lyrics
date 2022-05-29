package com.absan.verse.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import com.absan.verse.helpers.objects.ThemeHelper
import com.absan.verse.R
import com.addisonelliott.segmentedbutton.SegmentedButton
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup

fun changeThemeIcon(
    darkModeButton: SegmentedButton,
    autoModeButton: SegmentedButton,
    lightModeButton: SegmentedButton,
    themeToggle: SegmentedButtonGroup,
    context: Context,
    mainPrefInstance: SharedPreferences,
    position: Int,
    isAnimated: Boolean = true,
    isDarkThemeOn:Boolean = false
) {
    when (position) {
        0 -> {
            // Darkmode
            darkModeButton.drawable =
                ContextCompat.getDrawable(context, R.drawable.navbar__moonfill)
            darkModeButton.removeDrawableTint()

            autoModeButton.drawable =
                ContextCompat.getDrawable(context, R.drawable.navbar__autoframe)
            autoModeButton.drawableTint =
                ContextCompat.getColor(context, R.color.themeToggleNonActiveTint)

            lightModeButton.drawable =
                ContextCompat.getDrawable(context, R.drawable.navbar__sunframe)
            lightModeButton.drawableTint =
                ContextCompat.getColor(context, R.color.themeToggleNonActiveTint)

            themeToggle.setPosition(0, isAnimated)
            themeToggle.setOnPositionChangedListener {
                ThemeHelper.applyTheme("dark",isDarkThemeOn)
            }
            mainPrefInstance.edit().apply { putString("Theme", "dark") }.apply()
        }
        1 -> {
            // Automode
            darkModeButton.drawable =
                ContextCompat.getDrawable(context, R.drawable.navbar__moonframe)
            darkModeButton.drawableTint =
                ContextCompat.getColor(context, R.color.themeToggleNonActiveTint)

            autoModeButton.drawable =
                ContextCompat.getDrawable(context, R.drawable.navbar__autofill)
            autoModeButton.removeDrawableTint()

            lightModeButton.drawable =
                ContextCompat.getDrawable(context, R.drawable.navbar__sunframe)
            lightModeButton.drawableTint =
                ContextCompat.getColor(context, R.color.themeToggleNonActiveTint)

            themeToggle.setPosition(1, isAnimated)
            themeToggle.setOnPositionChangedListener {
                ThemeHelper.applyTheme("default",isDarkThemeOn)
            }
            mainPrefInstance.edit().apply { putString("Theme", "default") }.apply()
        }
        2 -> {
            // Lightmode
            darkModeButton.drawable =
                ContextCompat.getDrawable(context, R.drawable.navbar__moonframe)
            darkModeButton.drawableTint =
                ContextCompat.getColor(context, R.color.themeToggleNonActiveTint)

            autoModeButton.drawable =
                ContextCompat.getDrawable(context, R.drawable.navbar__autoframe)
            autoModeButton.drawableTint =
                ContextCompat.getColor(context, R.color.themeToggleNonActiveTint)

            lightModeButton.drawable =
                ContextCompat.getDrawable(context, R.drawable.navbar__sunfill)
            lightModeButton.removeDrawableTint()

            themeToggle.setPosition(2, isAnimated)
            themeToggle.setOnPositionChangedListener {
                ThemeHelper.applyTheme("light",isDarkThemeOn)
            }
            mainPrefInstance.edit().apply { putString("Theme", "light") }.apply()
        }
    }
}