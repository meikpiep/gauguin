package org.piepmeyer.gauguin

import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.Module
import org.koin.dsl.module
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.difficulty.GameDifficultyRatingService
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.game.GameSolveService
import org.piepmeyer.gauguin.game.save.CurrentGameSaver
import org.piepmeyer.gauguin.game.save.SavedGamesService
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.history.HistoryService
import java.io.File

class CoreModule(
    private val filesDir: File,
    private val initialGrid: Grid,
    private val applicationScope: CoroutineScope,
) {
    fun module(): Module =
        module {
            single {
                Game(
                    initialGrid,
                    InitialGridView(initialGrid),
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
                        initialGrid.variant,
                        get(),
                        get(),
                        get(),
                    )

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
