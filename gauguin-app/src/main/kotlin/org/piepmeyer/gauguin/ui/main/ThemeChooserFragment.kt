package org.piepmeyer.gauguin.ui.main

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.Theme
import org.piepmeyer.gauguin.databinding.FragmentThemeChooserBinding
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.ui.ActivityUtils
import org.piepmeyer.gauguin.ui.DynamicColorsPrecondition

class ThemeChooserFragment(
    private val mainActivity: Activity,
) : Fragment(R.layout.fragment_theme_chooser),
    KoinComponent {
    private val preferences: ApplicationPreferences by inject()
    private val activityUtils: ActivityUtils by inject()

    private lateinit var binding: FragmentThemeChooserBinding

    private var themeHasBeenAltered = false

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentThemeChooserBinding.inflate(inflater, parent, false)

        binding.navigationDrawerThemeLight.isChecked = preferences.theme == Theme.LIGHT
        binding.navigationDrawerThemeLight.setOnClickListener {
            themeHasBeenAltered = true
            preferences.theme = Theme.LIGHT
            activityUtils.configureTheme(mainActivity)
        }

        binding.navigationDrawerThemeDark.isChecked = preferences.theme == Theme.DARK
        binding.navigationDrawerThemeDark.setOnClickListener {
            themeHasBeenAltered = true
            preferences.theme = Theme.DARK
            activityUtils.configureTheme(mainActivity)
        }

        binding.navigationDrawerThemeAuto.isChecked = preferences.theme == Theme.SYSTEM_DEFAULT
        binding.navigationDrawerThemeAuto.setOnClickListener {
            themeHasBeenAltered = true
            preferences.theme = Theme.SYSTEM_DEFAULT
            activityUtils.configureTheme(mainActivity)
        }

        if (DynamicColors.isDynamicColorAvailable()) {
            binding.navigationDrawerThemeDynamicColors.isChecked =
                preferences.theme == Theme.DYNAMIC_COLORS
            binding.navigationDrawerThemeDynamicColors.setOnClickListener {
                themeHasBeenAltered = true
                preferences.theme = Theme.DYNAMIC_COLORS
                activityUtils.configureTheme(mainActivity)

                val options =
                    DynamicColorsOptions
                        .Builder()
                        .setThemeOverlay(R.style.AppTheme_Overlay)
                        .setPrecondition(DynamicColorsPrecondition())
                        .build()

                DynamicColors.applyToActivitiesIfAvailable(this.mainActivity.application, options)
            }
        } else {
            binding.navigationDrawerThemeDynamicColors.visibility = View.GONE
        }

        return binding.root
    }

    fun themeHasBeenAltered(): Boolean = themeHasBeenAltered
}
