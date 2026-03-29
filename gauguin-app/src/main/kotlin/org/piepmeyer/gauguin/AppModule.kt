package org.piepmeyer.gauguin

import android.content.Context.MODE_PRIVATE
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.binds
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.withOptions
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.ApplicationPreferencesImpl
import org.piepmeyer.gauguin.preferences.StatisticsManagerImpl
import org.piepmeyer.gauguin.preferences.StatisticsManagerReading
import org.piepmeyer.gauguin.preferences.StatisticsManagerWriting
import org.piepmeyer.gauguin.ui.ActivityUtils
import org.piepmeyer.gauguin.ui.main.MainViewModel
import org.piepmeyer.gauguin.ui.newgame.NewGameViewModel
import org.piepmeyer.gauguin.ui.statistics.StatisticsViewModel

class AppModule(
    private val applicationPreferences: ApplicationPreferencesImpl,
    private val applicationScope: CoroutineScope,
) {
    fun module(): Module =
        org.koin.dsl.module {
            single {
                applicationPreferences
            } withOptions { binds(listOf(ApplicationPreferences::class)) }
            single {
                DebugVariantServiceImpl(androidContext().resources)
            } withOptions { binds(listOf(DebugVariantService::class)) }
            single {
                StatisticsManagerImpl(
                    androidContext().filesDir,
                    androidContext().getSharedPreferences("stats", MODE_PRIVATE),
                )
            } withOptions {
                binds(listOf(StatisticsManagerReading::class, StatisticsManagerWriting::class))
                createdAtStart()
            }
            single { ActivityUtils() }
            single { MainViewModel(applicationScope) }
            viewModel { NewGameViewModel(get(), get(), get()) }
            viewModel { StatisticsViewModel(get()) }
        }
}
