package org.piepmeyer.gauguin.preferences

import android.content.SharedPreferences
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ApplicationPreferencesImplTest :
    FunSpec({

        data class NightModeData(
            val sharedPreferenceValue: String?,
            val expectedNightMode: NightMode,
        )

        withData(
            NightModeData(null, NightMode.DARK),
            NightModeData("unknown", NightMode.DARK),
            NightModeData("DARK", NightMode.DARK),
            NightModeData("LIGHT", NightMode.LIGHT),
            NightModeData("SYSTEM_DEFAULT", NightMode.SYSTEM_DEFAULT),
        ) { testData ->
            val sharedPreferences =
                mockk<SharedPreferences> {
                    every { getString("nightMode", null) } returns testData.sharedPreferenceValue
                }

            val preferences =
                ApplicationPreferencesImpl(
                    mockk(),
                    sharedPreferences,
                )

            preferences.nightMode shouldBe testData.expectedNightMode
        }

        data class ThemeTestData(
            val sharedPreferenceValue: String?,
            val expectedTheme: Theme,
        )

        withData(
            ThemeTestData(null, Theme.GAUGUIN),
            ThemeTestData("unknown", Theme.GAUGUIN),
            ThemeTestData("DYNAMIC_COLORS", Theme.DYNAMIC_COLORS),
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
