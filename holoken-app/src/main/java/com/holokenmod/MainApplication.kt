package com.holokenmod

import android.app.Application
import androidx.preference.PreferenceManager
import com.holokenmod.calculation.GridCalculationService
import com.holokenmod.game.Game
import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridSize
import com.holokenmod.grid.GridView
import com.holokenmod.options.ApplicationPreferences
import com.holokenmod.options.GameOptionsVariant
import com.holokenmod.options.GameVariant
import com.holokenmod.undo.UndoListener
import com.holokenmod.undo.UndoManager
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
                    initialGame()
                }
                single {
                    GridCalculationService()
                }
                single {
                    ApplicationPreferences(
                        PreferenceManager.getDefaultSharedPreferences(this@MainApplication)
                    )
                }
            }

            modules(appModule)
        }
    }

    private fun initialGame() = Game(
        Grid(
            GameVariant(
                GridSize(9, 9),
                GameOptionsVariant.createClassic()
            )
        ),
        UndoManager(object : UndoListener {
            override fun undoStateChanged(undoPossible: Boolean) {

            }
        }),
        object : GridView {
            override fun requestFocus() = false

            override fun invalidate() {}
        }
    )
}