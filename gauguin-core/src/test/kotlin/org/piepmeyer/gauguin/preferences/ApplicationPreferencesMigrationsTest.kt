package org.piepmeyer.gauguin.preferences

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.piepmeyer.gauguin.NightMode
import org.piepmeyer.gauguin.Theme

class ApplicationPreferencesMigrationsTest :
    FunSpec({
        test("night mode migration gets not triggered if night mode is already in use") {
            val preferences =
                mockk<ApplicationPreferences> {
                    every { getString("nightMode", null) } returns "DARK"
                    // no setter of 'nightMode' or 'theme' allowed here
                }

            val migrations = ApplicationPreferencesMigrations(preferences)

            migrations.migrateThemeToNightModeIfNecessary()
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
            val preferences =
                mockk<ApplicationPreferences> {
                    every { getString("nightMode", null) } returns null
                    every { getString("theme", null) } returns testData.sharedPreferenceValue
                    every { theme = testData.expectedTheme } just runs
                    every { nightMode = testData.expectedNightMode } just runs
                }

            val migrations = ApplicationPreferencesMigrations(preferences)

            migrations.migrateThemeToNightModeIfNecessary()

            verify {
                preferences.theme = testData.expectedTheme
                preferences.nightMode = testData.expectedNightMode
            }
        }
    })
