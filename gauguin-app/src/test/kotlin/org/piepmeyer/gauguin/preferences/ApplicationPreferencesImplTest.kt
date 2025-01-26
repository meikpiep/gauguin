package org.piepmeyer.gauguin.preferences

import android.content.SharedPreferences
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.piepmeyer.gauguin.NightMode
import org.piepmeyer.gauguin.Theme

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

        data class OldThemeMigrationTestData(
            val sharedPreferenceValue: String?,
            val expectedTheme: Theme,
            val expectedNightMode: NightMode,
        )

        withData(
            OldThemeMigrationTestData(null, Theme.GAUGUIN, NightMode.DARK),
            OldThemeMigrationTestData("unknown", Theme.GAUGUIN, NightMode.DARK),
            OldThemeMigrationTestData("DARK", Theme.GAUGUIN, NightMode.DARK),
            OldThemeMigrationTestData("LIGHT", Theme.GAUGUIN, NightMode.LIGHT),
            OldThemeMigrationTestData("DYNAMIC_COLORS", Theme.DYNAMIC_COLORS, NightMode.LIGHT),
            OldThemeMigrationTestData("SYSTEM_DEFAULT", Theme.GAUGUIN, NightMode.SYSTEM_DEFAULT),
        ) { testData ->
            val sharedPreferences =
                mockk<SharedPreferences>()

            val preferences =
                ApplicationPreferencesImpl(
                    mockk(),
                    sharedPreferences,
                )

            val (theme, nightMode) = preferences.migrateToNewThemeNightModesValues(testData.sharedPreferenceValue)

            assertSoftly {
                withClue("theme") {
                    theme shouldBe testData.expectedTheme
                }
                withClue("nightMode") {
                    nightMode shouldBe testData.expectedNightMode
                }
            }
        }
    })
