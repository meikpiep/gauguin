package org.piepmeyer.gauguin.preferences

import android.content.SharedPreferences
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.piepmeyer.gauguin.Theme

class ApplicationPreferencesImplTest :
    FunSpec({

        data class ThemeTestData(
            val sharedPreferenceValue: String?,
            val expectedTheme: Theme,
        )

        withData(
            ThemeTestData(null, Theme.DARK),
            ThemeTestData("unknown", Theme.DARK),
            ThemeTestData("DARK", Theme.DARK),
            ThemeTestData("LIGHT", Theme.LIGHT),
            ThemeTestData("DYNAMIC_COLORS", Theme.DYNAMIC_COLORS),
            ThemeTestData("SYSTEM_DEFAULT", Theme.SYSTEM_DEFAULT),
        ) { testData ->
            val sharedPreferences =
                mockk<SharedPreferences> {
                    every { getString("theme", null) } returns testData.sharedPreferenceValue
                }

            val preferences =
                ApplicationPreferencesImpl(
                    mockk(),
                    sharedPreferences,
                )

            preferences.theme shouldBe testData.expectedTheme
        }
    })
