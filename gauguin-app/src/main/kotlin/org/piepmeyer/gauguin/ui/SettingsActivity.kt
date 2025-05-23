package org.piepmeyer.gauguin.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.preference.PreferenceFragmentCompat
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R

class SettingsActivity : AppCompatActivity() {
    private val activityUtils: ActivityUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        activityUtils.configureTheme(this)
        setContentView(R.layout.activity_settings)
        activityUtils.configureMainContainerBackground(findViewById(R.id.rootSettings))
        activityUtils.configureRootView(findViewById(R.id.rootSettings))

        if (savedInstanceState == null) {
            val settings = SettingsFragment()

            supportFragmentManager.commit {
                replace(R.id.settings, settings)
            }
        }
        val actionBar = supportActionBar

        activityUtils.configureFullscreen(this)

        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(
            savedInstanceState: Bundle?,
            rootKey: String?,
        ) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}
