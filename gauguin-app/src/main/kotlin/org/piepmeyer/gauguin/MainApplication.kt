package org.piepmeyer.gauguin

import HumanSolverModule
import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.binds
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import org.piepmeyer.gauguin.game.save.SavedGamesService
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.ApplicationPreferencesImpl
import org.piepmeyer.gauguin.preferences.ApplicationPreferencesMigrations
import org.piepmeyer.gauguin.preferences.StatisticsManagerImpl
import org.piepmeyer.gauguin.preferences.StatisticsManagerReading
import org.piepmeyer.gauguin.preferences.StatisticsManagerWriting
import org.piepmeyer.gauguin.ui.ActivityUtils
import org.piepmeyer.gauguin.ui.DynamicColorsPrecondition

private val logger = KotlinLogging.logger {}

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        logger.info { "Starting application Gauguin..." }

        SavedGamesService.migrateOldSavedGameFilesBeforeKoinStartup(filesDir)

        val applicationPreferences = ApplicationPreferencesImpl(this)
        val preferenceMigrations = ApplicationPreferencesMigrations(applicationPreferences)
        preferenceMigrations.migrateThemeToNightModeIfNecessary()
        preferenceMigrations.migrateDifficultySettingIfNecessary()

        val options =
            DynamicColorsOptions
                .Builder()
                .setThemeOverlay(R.style.AppTheme_Overlay)
                .setPrecondition(DynamicColorsPrecondition())
                .build()

        DynamicColors.applyToActivitiesIfAvailable(this, options)

        val applicationScope = CoroutineScope(SupervisorJob())

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
                        binds(listOf(StatisticsManagerReading::class, StatisticsManagerWriting::class))
                        createdAtStart()
                    }
                    single { ActivityUtils() }
                }

            applicationPreferences.migrateGridSizeFromTwoToThree()

            modules(
                CoreModule(filesDir, applicationScope).module(),
                HumanSolverModule().module(),
                appModule,
            )
        }

        if (!avoidNightModeConfigurationForTest) {
            logger.info { "Configuring night mode..." }
            get<ActivityUtils>().configureNightMode()
            logger.info { "Configuring night mode done." }
        }

        logger.info {
            "Gauguin application started successfully, " +
                "version ${resources.getString(R.string.versionName)}, " +
                "debug flag ${resources.getBoolean(R.bool.debuggable)}."
        }
    }

    companion object {
        var avoidNightModeConfigurationForTest: Boolean = false
    }
}
