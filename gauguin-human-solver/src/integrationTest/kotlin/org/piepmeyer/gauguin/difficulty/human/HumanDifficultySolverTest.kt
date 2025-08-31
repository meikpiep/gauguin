package org.piepmeyer.gauguin.difficulty.human

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.MergingCageGridCalculator
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.game.save.SaveGame
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import java.io.File

class HumanDifficultySolverTest :
    FunSpec({
        for (seed in 0..9999) {
            withClue("seed $seed") {
                xtest("seed random grid should be solved") {
                    val randomizer = SeedRandomizerMock(seed)

                    val calculator =
                        MergingCageGridCalculator(
                            GameVariant(
                                GridSize(5, 5),
                                GameOptionsVariant.createClassic(),
                            ),
                            randomizer,
                            RandomPossibleDigitsShuffler(randomizer.random),
                        )

                    val grid = calculator.calculate()
                    grid.cells.forEach { it.possibles = grid.variant.possibleDigits }

                    val solver = HumanSolver(grid, true)

                    val solverResult = solver.solveAndCalculateDifficulty(true)

                    println(grid.toString())

                    if (!solverResult.success) {
                        if (grid.numberOfMistakes() != 0) {
                            throw IllegalStateException("Found a grid with wrong values.")
                        }
                        grid.isActive = true
                        grid.startedToBePlayed = true
                        grid.description = "${grid.gridSize.width}x${grid.gridSize.height}-$seed"
                        val saveGame =
                            SaveGame.createWithFile(
                                File(
                                    SaveGame.SAVEGAME_NAME_PREFIX +
                                        "${grid.numberOfMistakes()}-${grid.gridSize.width}x${grid.gridSize.height}-$seed.yml",
                                ),
                            )

                        saveGame.save(grid)
                    }

                    solverResult.success shouldBe true

                    solverResult.difficulty shouldBeGreaterThan 0
                }
            }
        }
    })
