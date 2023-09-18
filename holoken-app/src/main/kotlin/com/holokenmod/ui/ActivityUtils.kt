package com.holokenmod.ui

import android.app.Activity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.holokenmod.Theme
import com.holokenmod.options.ApplicationPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ActivityUtils: KoinComponent {
    private val applicationPreferences: ApplicationPreferences by inject()

    fun configureFullscreen(activity: Activity, view: View) {
        if (!applicationPreferences.preferences.getBoolean("showfullscreen", false)) {
            WindowInsetsControllerCompat(activity.window, view).show(WindowInsetsCompat.Type.statusBars())
        } else {
            WindowInsetsControllerCompat(activity.window, view).hide(WindowInsetsCompat.Type.statusBars())
        }
    }

    fun configureKeepScreenOn(activity: Activity) {
        if (applicationPreferences.preferences.getBoolean("keepscreenon", true)) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    fun configureNightMode() {
        val theme: Theme = applicationPreferences.theme
        if (theme === Theme.LIGHT) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else if (theme === Theme.DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}
