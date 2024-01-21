package org.piepmeyer.gauguin.ui

import android.app.Activity
import com.google.android.material.color.DynamicColors.Precondition
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.Theme
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

class DynamicColorsPrecondition : KoinComponent, Precondition {
    private val applicationPreferences: ApplicationPreferences by inject()

    override fun shouldApplyDynamicColors(
        activity: Activity,
        theme: Int,
    ): Boolean {
        return applicationPreferences.theme == Theme.DYNAMIC_COLORS
    }
}
