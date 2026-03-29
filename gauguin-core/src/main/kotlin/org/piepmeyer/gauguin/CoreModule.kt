package org.piepmeyer.gauguin

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.module.Module
import org.koin.dsl.module
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.difficulty.GameDifficultyRatingService
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.game.GameSolveService
import org.piepmeyer.gauguin.game.save.CurrentGameSaver
import org.piepmeyer.gauguin.game.save.SavedGamesService
import org.piepmeyer.gauguin.history.HistoryService
import java.io.File

class CoreModule(
    private val filesDir: File,
    private val applicationScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    fun module(): Module {
        val grid = InitialGridLoader(filesDir).initialGrid()

        return module {
            single {
                Game(
                    grid,
                    InitialGridView(grid),
                    get(),
                    get(),
                )
            }
            single {
                GameLifecycle(
                    filesDir,
                    applicationScope,
                    get(),
                    get(),
                    get(),
                    get(),
                )
            }
            single {
                val calculationService =
                    GridCalculationService(
                        grid.variant,
                        get(),
                        get(),
                        get(),
                    )

                applicationScope.launch(ioDispatcher) {
                    calculationService.loadNextGrid()
                }

                calculationService
            }
            single {
                SavedGamesService(filesDir)
            }
            single {
                GameSolveService(
                    get(),
                    get(),
                )
            }
            single {
                CurrentGameSaver(
                    filesDir,
                    get(),
                    get(),
                )
            }
            single { GameDifficultyRatingService() }
            single { HistoryService() }
        }
    }
}
