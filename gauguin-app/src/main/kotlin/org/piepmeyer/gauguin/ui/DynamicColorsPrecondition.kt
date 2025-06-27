package org.piepmeyer.gauguin.ui

import android.app.Activity
import com.google.android.material.color.DynamicColors.Precondition
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.Theme

class DynamicColorsPrecondition :
    KoinComponent,
    Precondition {
    private val applicationPreferences: ApplicationPreferences by inject()

    override fun shouldApplyDynamicColors(
        activity: Activity,
        theme: Int,
    ): Boolean = applicationPreferences.theme == Theme.DYNAMIC_COLORS
}
