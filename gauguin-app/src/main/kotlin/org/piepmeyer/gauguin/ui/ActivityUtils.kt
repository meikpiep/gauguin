package org.piepmeyer.gauguin.ui

import android.app.Activity
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import org.piepmeyer.gauguin.Theme
import org.piepmeyer.gauguin.options.ApplicationPreferencesImpl
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ActivityUtils: KoinComponent {
    private val applicationPreferences: ApplicationPreferencesImpl by inject()

    @Suppress("DEPRECATION")
    fun configureFullscreen(activity: Activity) {
        if (!applicationPreferences.preferences.getBoolean("showfullscreen", false)) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
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
