package org.piepmeyer.gauguin

import HumanSolverModule
import android.app.Application
import android.os.StrictMode
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.binds
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import org.piepmeyer.gauguin.creation.GridCreationViaMergeModule
import org.piepmeyer.gauguin.game.save.SavedGamesService
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.ApplicationPreferencesImpl
import org.piepmeyer.gauguin.preferences.ApplicationPreferencesMigrations
import org.piepmeyer.gauguin.preferences.StatisticsManagerImpl
import org.piepmeyer.gauguin.preferences.StatisticsManagerReading
import org.piepmeyer.gauguin.preferences.StatisticsManagerWriting
import org.piepmeyer.gauguin.ui.ActivityUtils
import org.piepmeyer.gauguin.ui.DynamicColorsPrecondition
import org.piepmeyer.gauguin.ui.main.MainViewModel

private val logger = KotlinLogging.logger {}

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        logger.info { "Starting application Gauguin..." }

        println("main dispatcher: ${kotlinx.coroutines.android.HandlerDispatcher::class.java}")

        if (resources.getBoolean(R.bool.debuggable)) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy
                    .Builder()
                    .detectAll()
                    .penaltyDeath()
                    .build(),
            )
        }

        val applicationPreferences =
            runBlocking {
                async(Dispatchers.IO) {
                    val preferences = ApplicationPreferencesImpl(this@MainApplication)

                    val preferenceMigrations = ApplicationPreferencesMigrations(preferences)
                    preferenceMigrations.migrateThemeToNightModeIfNecessary()
                    preferenceMigrations.migrateDifficultySettingIfNecessary()

                    preferences
                }.await()
            }

        val appFilesDir =
            runBlocking {
                async(Dispatchers.IO) {
                    filesDir
                }.await()
            }

        val options =
            DynamicColorsOptions
                .Builder()
                .setThemeOverlay(R.style.AppTheme_Overlay)
                .setPrecondition(DynamicColorsPrecondition())
                .build()

        DynamicColors.applyToActivitiesIfAvailable(this, options)

        val applicationScope = CoroutineScope(SupervisorJob())

        startKoin {
            allowOverride(true)
            androidLogger()
            androidContext(this@MainApplication)

            val appModule =
                module {
                    single {
                        applicationPreferences
                    } withOptions { binds(listOf(ApplicationPreferences::class)) }
                    single {
                        DebugVariantServiceImpl(this.androidContext().resources)
                    } withOptions { binds(listOf(DebugVariantService::class)) }
                    single {
                        StatisticsManagerImpl(
                            appFilesDir,
                            // this@MainApplication.getSharedPreferences("stats", MODE_PRIVATE),
                        )
                    } withOptions {
                        binds(listOf(StatisticsManagerReading::class, StatisticsManagerWriting::class))
                        createdAtStart()
                    }
                    single { ActivityUtils() }
                    single { MainViewModel(applicationScope) }
                }

            applicationPreferences.migrateGridSizeFromTwoToThree()

            val listOfModules =
                mutableListOf(
                    CoreModule(appFilesDir, applicationScope).module(),
                    HumanSolverModule().module(),
                    GridCreationViaMergeModule().module(),
                    appModule,
                )

            overrideTestModule?.let {
                listOfModules += it
            }

            modules(listOfModules)
        }

        if (!avoidNightModeConfigurationForTest) {
            get<ActivityUtils>().configureNightMode()
        }

        applicationScope.launch(Dispatchers.IO) {
            SavedGamesService.migrateOldSavedGameFilesBeforeKoinStartup(appFilesDir)
        }

        logger.info {
            "Gauguin application started successfully, " +
                "version ${resources.getString(R.string.versionName)}, " +
                "debug flag ${resources.getBoolean(R.bool.debuggable)}."
        }
    }

    companion object {
        var avoidNightModeConfigurationForTest: Boolean = false
        var overrideTestModule: Module? = null
    }
}
