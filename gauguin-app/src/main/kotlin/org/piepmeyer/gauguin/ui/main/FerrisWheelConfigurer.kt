package org.piepmeyer.gauguin.ui.main

import android.content.res.Configuration
import android.graphics.Color
import com.google.android.material.color.MaterialColors
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.Theme
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import ru.github.igla.ferriswheel.CabinStyle
import ru.github.igla.ferriswheel.CoreStyle
import ru.github.igla.ferriswheel.FerrisWheelView
import ru.github.igla.ferriswheel.StarIcon

class FerrisWheelConfigurer(
    private val ferrisWheel: FerrisWheelView,
) : KoinComponent {
    private val applicationPreferences: ApplicationPreferences by inject()

    fun configure() {
        ferrisWheel.baseColor =
            MaterialColors.getColor(ferrisWheel, com.google.android.material.R.attr.colorOnSurfaceVariant)
        ferrisWheel.wheelColor =
            MaterialColors.getColor(ferrisWheel, com.google.android.material.R.attr.colorOnSurfaceVariant)
        ferrisWheel.coreStyle =
            CoreStyle(
                MaterialColors.getColor(ferrisWheel, com.google.android.material.R.attr.colorSecondary),
                MaterialColors.getColor(ferrisWheel, com.google.android.material.R.attr.colorOnSecondary),
                StarIcon(MaterialColors.getColor(ferrisWheel, com.google.android.material.R.attr.colorSurfaceVariant)),
            )
        ferrisWheel.cabinColors =
            when (applicationPreferences.theme) {
                Theme.DARK -> cabinColorsDark()
                Theme.LIGHT -> cabinColorsLight()
                Theme.DYNAMIC_COLORS -> cabinColorsDynamic()
                Theme.SYSTEM_DEFAULT -> {
                    when (isNightMode()) {
                        true -> cabinColorsDark()
                        false -> cabinColorsLight()
                    }
                }
            }

        ferrisWheel.numberOfCabins = 9
    }

    private fun isNightMode(): Boolean {
        val mode =
            ferrisWheel.context
                ?.resources
                ?.configuration
                ?.uiMode ?: return false

        return (mode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    private fun cabinColorsDark(): List<CabinStyle> =
        listOf(
            cabin(com.google.android.material.R.attr.colorPrimaryVariant),
            cabin(com.google.android.material.R.attr.colorSecondaryVariant),
            cabinFromColor(R.color.gridSelected),
        )

    private fun cabinColorsLight(): List<CabinStyle> =
        listOf(
            cabin(com.google.android.material.R.attr.colorSecondaryVariant),
            cabin(R.attr.colorMainTopPanelBackground),
            cabinFromColor(R.color.md_theme_light_inversePrimary),
        )

    private fun cabinColorsDynamic(): List<CabinStyle> =
        listOf(
            cabin(com.google.android.material.R.attr.colorPrimaryVariant),
            cabin(com.google.android.material.R.attr.colorSecondaryVariant),
            cabin(R.attr.colorMainTopPanelBackground),
        )

    private fun cabin(attr: Int) =
        CabinStyle(
            MaterialColors.getColor(ferrisWheel, attr),
            Color.TRANSPARENT,
        )

    private fun cabinFromColor(color: Int) =
        CabinStyle(
            ferrisWheel.resources.getColor(color, null),
            Color.TRANSPARENT,
        )
}
