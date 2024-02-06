package org.piepmeyer.gauguin

import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.binds
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.ApplicationPreferencesImpl
import org.piepmeyer.gauguin.preferences.StatisticsManager
import org.piepmeyer.gauguin.preferences.StatisticsManagerImpl
import org.piepmeyer.gauguin.ui.ActivityUtils
import org.piepmeyer.gauguin.ui.DynamicColorsPrecondition
import org.piepmeyer.gauguin.ui.grid.GridCellSizeService

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val applicationPreferences = ApplicationPreferencesImpl(this)

        val options =
            DynamicColorsOptions.Builder()
                .setThemeOverlay(R.style.AppTheme_Overlay)
                .setPrecondition(DynamicColorsPrecondition())
                .build()

        DynamicColors.applyToActivitiesIfAvailable(this, options)

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)

            val appModule =
                module {
                    single {
                        applicationPreferences
                    } withOptions { binds(listOf(ApplicationPreferences::class)) }
                    single {
                        StatisticsManagerImpl(
                            filesDir,
                            this@MainApplication.getSharedPreferences("stats", Context.MODE_PRIVATE),
                        )
                    } withOptions {
                        binds(listOf(StatisticsManager::class))
                        createdAtStart()
                    }
                    single {
                        GridCellSizeService()
                    }
                    single { ActivityUtils() }
                }

            applicationPreferences.migrateGridSizeFromTwoToThree()

            modules(
                CoreModule(filesDir).module(),
                appModule,
            )
        }
    }
}
