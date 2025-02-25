package org.piepmeyer.gauguin.preferences

import org.piepmeyer.gauguin.NightMode
import org.piepmeyer.gauguin.Theme

class ApplicationPreferencesMigrations(
    private val applicationPreferences: ApplicationPreferences,
) {
    fun migrateThemeToNightModeIfNecessary() {
        if (applicationPreferences.getString("nightMode", null) != null) {
            return
        }

        val oldThemeValue = applicationPreferences.getString("theme", null)
        /*
         * Possible values:
         * LIGHT
         * DARK
         * SYSTEM_DEFAULT
         * DYNAMIC_COLORS
         */

        val (newThemeValue, newNightModeValue) = migrateToNewThemeNightModesValues(oldThemeValue)

        applicationPreferences.theme = newThemeValue
        applicationPreferences.nightMode = newNightModeValue
    }

    private fun migrateToNewThemeNightModesValues(oldThemeValue: String?): Pair<Theme, NightMode> {
        val newThemeValue = if (oldThemeValue == "DYNAMIC_COLORS") Theme.DYNAMIC_COLORS else Theme.GAUGUIN

        val newNightModeValue =
            when (oldThemeValue) {
                "LIGHT", "DYNAMIC_COLORS" -> NightMode.LIGHT
                "DARK" -> NightMode.DARK
                "SYSTEM_DEFAULT" -> NightMode.SYSTEM_DEFAULT
                else -> NightMode.DARK
            }

        return Pair(newThemeValue, newNightModeValue)
    }
}
