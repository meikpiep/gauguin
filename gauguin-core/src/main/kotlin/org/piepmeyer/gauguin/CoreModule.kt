package org.piepmeyer.gauguin

import kotlinx.coroutines.runBlocking
import org.koin.core.module.Module
import org.koin.dsl.module
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.creation.GridCalculator
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.game.GameSolveService
import org.piepmeyer.gauguin.game.save.CurrentGameSaver
import org.piepmeyer.gauguin.game.save.MigrateOldSavedGamesService
import org.piepmeyer.gauguin.game.save.SaveGame
import org.piepmeyer.gauguin.game.save.SavedGamesService
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.grid.GridView
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.StatisticsManager
import org.piepmeyer.gauguin.undo.UndoManager
import java.io.File

class CoreModule(
    private val filesDir: File,
) {
    fun module(): Module =
        module {
            single {
                MigrateOldSavedGamesService(filesDir).deleteOldSaveGameFiles()

                val grid = initialGrid()

                Game(
                    grid,
                    UndoManager { },
                    initialGridView(grid),
                    get(StatisticsManager::class),
                    get(ApplicationPreferences::class),
                )
            }
            single {
                GameLifecycle(
                    filesDir,
                    get(Game::class),
                    get(ApplicationPreferences::class),
                    get(GridCalculationService::class),
                    get(StatisticsManager::class),
                )
            }
            single {
                GridCalculationService(initialGameVariant())
            }
            single {
                SavedGamesService(filesDir)
            }
            single {
                GameSolveService(get(Game::class))
            }
            single {
                CurrentGameSaver(
                    filesDir,
                    get(Game::class),
                    get(SavedGamesService::class),
                )
            }
        }

    private fun initialGrid(): Grid {
        SaveGame.autosaveByDirectory(this.filesDir).restore()?.let {
            return it
        }

        return runBlocking {
            val grid = GridCalculator(initialGameVariant()).calculate()
            grid.isActive = true

            grid
        }
    }

    private fun initialGameVariant(): GameVariant {
        return GameVariant(
            GridSize(6, 6),
            GameOptionsVariant.createClassic(),
        )
    }

    private fun initialGridView(grid: Grid): GridView {
        return object : GridView {
            override var grid: Grid
                get() = grid
                set(_) {
                    // dummy implementation
                }

            override fun requestFocus() = false

            override fun invalidate() {
                // dummy implementation
            }
        }
    }
}
