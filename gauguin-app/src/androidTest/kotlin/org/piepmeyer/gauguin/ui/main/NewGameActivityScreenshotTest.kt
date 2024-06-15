package org.piepmeyer.gauguin.ui.main

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.testify.ScreenshotRule
import dev.testify.annotation.ScreenshotInstrumentation
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.ui.newgame.NewGameActivity

@RunWith(AndroidJUnit4::class)
class NewGameActivityScreenshotTest : KoinComponent {
    @get:Rule
    val rule =
        ScreenshotRule(NewGameActivity::class.java)
            .configure {
                focusTargetId = R.id.startnewgame
                // orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

    private val preferences: ApplicationPreferences by inject()

    @ScreenshotInstrumentation
    @Test
    fun newGameUntouched() {
        rule.setViewModifications {
            preferences.clear()
            preferences.gridTakesRemainingSpaceIfNecessary = false
        }

        rule.assertSame()
    }
}
