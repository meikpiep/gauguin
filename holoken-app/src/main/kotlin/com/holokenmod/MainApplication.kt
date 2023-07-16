package com.holokenmod

import android.app.Application
import androidx.preference.PreferenceManager
import com.holokenmod.options.ApplicationPreferences
import com.holokenmod.ui.grid.GridCellSizeService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidLogger()
            androidContext(this@MainApplication)

            val appModule = module {
                single {
                    ApplicationPreferences(
                        PreferenceManager.getDefaultSharedPreferences(this@MainApplication)
                    )
                }
                single {
                    GridCellSizeService()
                }
            }

            modules(
                CoreModule(filesDir).module(),
                appModule
            )
        }
    }
}
