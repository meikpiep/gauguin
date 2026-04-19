package org.piepmeyer.gauguin

import HumanSolverModule
import android.app.Application
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.piepmeyer.gauguin.creation.GridCreationViaMergeModule
import org.piepmeyer.gauguin.game.save.SavedGamesService
import org.piepmeyer.gauguin.preferences.ApplicationPreferencesImpl
import org.piepmeyer.gauguin.preferences.ApplicationPreferencesMigrations
import org.piepmeyer.gauguin.ui.ActivityUtils
import org.piepmeyer.gauguin.ui.DynamicColorsPrecondition

private val logger = KotlinLogging.logger {}

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        logger.info { "Starting application Gauguin..." }

        val applicationPreferences = ApplicationPreferencesImpl(this)
        val preferenceMigrations = ApplicationPreferencesMigrations(applicationPreferences)
        preferenceMigrations.migrateThemeToNightModeIfNecessary()
        preferenceMigrations.migrateDifficultySettingIfNecessary()

        enableDynamicColors()

        val applicationScope = CoroutineScope(SupervisorJob())

        startKoin {
            allowOverride(true)
            androidLogger()
            androidContext(this@MainApplication)

            val appModule = AppModule(applicationPreferences, applicationScope).module()

            applicationPreferences.migrateGridSizeFromTwoToThree()

            val listOfModules =
                mutableListOf(
                    CoreModule(filesDir, applicationScope).module(),
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
            SavedGamesService.migrateOldSavedGameFilesBeforeKoinStartup(filesDir)
        }

        logger.info {
            "Gauguin application started successfully, " +
                "version ${resources.getString(R.string.versionName)}, " +
                "debug flag ${resources.getBoolean(R.bool.debuggable)}."
        }
    }

    private fun enableDynamicColors() {
        val options =
            DynamicColorsOptions
                .Builder()
                .setThemeOverlay(R.style.AppTheme_Overlay)
                .setPrecondition(DynamicColorsPrecondition())
                .build()

        DynamicColors.applyToActivitiesIfAvailable(this, options)
    }

    companion object {
        var avoidNightModeConfigurationForTest: Boolean = false
        var overrideTestModule: Module? = null
    }
}
