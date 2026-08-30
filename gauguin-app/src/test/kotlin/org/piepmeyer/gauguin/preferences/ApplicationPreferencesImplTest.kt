package org.piepmeyer.gauguin.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

val dataStore: ReadWriteProperty<Context, DataStore<Preferences>> by preferencesDataStore(name = "settings")

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
            val sharedPreferenceValue: String,
            val expectedTheme: Theme,
        )

        withData(
            ThemeTestData("unknown", Theme.GAUGUIN),
            ThemeTestData("DYNAMIC_COLORS", Theme.DYNAMIC_COLORS),
        ) { testData ->

            MultiProcessDataStoreFactory
            dataStore.updateData {
                it.toMutablePreferences().also { preferences ->
                    preferences[ApplicationPreferencesImpl.keyTheme] = testData.sharedPreferenceValue
                }
            }

            val androidContext =
                mockk<Context> {
                    every { dataStore } returns dataStore
                }
            val sharedPreferences =
                mockk<SharedPreferences> {
                    every { getString("theme", null) } returns testData.sharedPreferenceValue
                }

            val preferences =
                ApplicationPreferencesImpl(
                    androidContext,
                    sharedPreferences,
                )

            preferences.getTheme() shouldBe testData.expectedTheme
        }
    })
