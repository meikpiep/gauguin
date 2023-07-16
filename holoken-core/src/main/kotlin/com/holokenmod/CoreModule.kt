package com.holokenmod

import com.holokenmod.calculation.GridCalculationService
import com.holokenmod.creation.GridCalculator
import com.holokenmod.game.Game
import com.holokenmod.game.SaveGame
import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridSize
import com.holokenmod.grid.GridView
import com.holokenmod.options.GameOptionsVariant
import com.holokenmod.options.GameVariant
import com.holokenmod.undo.UndoListener
import com.holokenmod.undo.UndoManager
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

class CoreModule(
    private val filesDir: File
) {

    fun module(): Module = module {
        single {
            initialGame()
        }
        single {
            GridCalculationService(initialGameVariant())
        }
    }

    private fun initialGame(): Game {
        val grid = initialGrid()

        return Game(
            grid,
            UndoManager(object : UndoListener {
                override fun undoStateChanged(undoPossible: Boolean) {}
            }),
            initialGridView(grid)
        )
    }

    private fun initialGrid(): Grid {
        return SaveGame.createWithDirectory(this.filesDir).restore()
            ?: GridCalculator(initialGameVariant()).calculate().also { it.isActive = true }
    }

    private fun initialGameVariant(): GameVariant {
        return GameVariant(
            GridSize(4, 4),
            GameOptionsVariant.createClassic()
        )
    }

    private fun initialGridView(grid: Grid): GridView {
        return object : GridView {
            override var grid: Grid
                get() = grid
                set(_) {}

            override fun requestFocus() = false

            override fun invalidate() {}
        }
    }
}
