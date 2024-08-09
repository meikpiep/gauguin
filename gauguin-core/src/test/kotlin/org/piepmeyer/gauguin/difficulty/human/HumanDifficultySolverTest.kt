package org.piepmeyer.gauguin.difficulty.human

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.RandomCageGridCalculator
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
            // current try 10_000 of 4x4: 19 left unsolved
            withClue("seed $seed") {
                test("seed random grid should be solved") {
                    val randomizer = SeedRandomizerMock(seed)

                    val calculator =
                        RandomCageGridCalculator(
                            GameVariant(
                                GridSize(4, 4),
                                GameOptionsVariant.createClassic(),
                            ),
                            randomizer,
                            RandomPossibleDigitsShuffler(randomizer.random),
                        )

                    val grid = calculator.calculate()
                    grid.cells.forEach { it.possibles = grid.variant.possibleDigits }

                    val solver = HumanSolver(grid)

                    val solverResult = solver.solveAndCalculateDifficulty()

                    println(grid.toString())

                    if (!grid.isSolved()) {
                        grid.isActive = true
                        grid.startedToBePlayed = true
                        val saveGame =
                            SaveGame.createWithFile(
                                File(
                                    SaveGame.SAVEGAME_NAME_PREFIX +
                                        "${grid.numberOfMistakes()}-${grid.gridSize.width}x${grid.gridSize.height}-$seed.yml",
                                ),
                            )

                        saveGame.save(grid)
                    }

                    grid.isSolved() shouldBe true

                    solverResult.difficulty shouldBeGreaterThan 0
                }
            }
        }
    })
