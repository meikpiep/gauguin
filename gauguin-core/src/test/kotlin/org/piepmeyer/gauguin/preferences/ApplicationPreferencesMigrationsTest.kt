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
import org.piepmeyer.gauguin.options.DifficultySetting

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

        test("difficulty migration gets not triggered if difficulties are already in use") {
            val preferences =
                mockk<ApplicationPreferences> {
                    every { getStringSet("difficulties", null) } returns setOf(DifficultySetting.EASY.name)
                    // no setter of 'difficulties' allowed here
                }

            val migrations = ApplicationPreferencesMigrations(preferences)

            migrations.migrateDifficultySettingIfNecessary()
        }

        test("difficulty migration gets not triggered if no difficulty preference is used yet") {
            val preferences =
                mockk<ApplicationPreferences> {
                    every { getStringSet("difficulties", null) } returns null
                    every { getString("difficulty", null) } returns null
                    // no setter of 'difficulties' allowed here
                }

            val migrations = ApplicationPreferencesMigrations(preferences)

            migrations.migrateDifficultySettingIfNecessary()
        }

        data class DifficultyMigrationTestData(
            val sharedPreferenceDifficultyValue: String?,
            val expectedDifficultiesValue: Set<DifficultySetting>,
        )

        withData(
            DifficultyMigrationTestData("EASY", setOf(DifficultySetting.EASY)),
            DifficultyMigrationTestData("EXTREME", setOf(DifficultySetting.EXTREME)),
            DifficultyMigrationTestData("ANY", DifficultySetting.all()),
        ) { testData ->
            val preferences =
                mockk<ApplicationPreferences> {
                    every { getStringSet("difficulties", null) } returns null
                    every { getString("difficulty", null) } returns testData.sharedPreferenceDifficultyValue
                    every { difficultiesSetting = testData.expectedDifficultiesValue } just runs
                }

            val migrations = ApplicationPreferencesMigrations(preferences)

            migrations.migrateDifficultySettingIfNecessary()

            verify {
                preferences.difficultiesSetting = testData.expectedDifficultiesValue
            }
        }
    })
