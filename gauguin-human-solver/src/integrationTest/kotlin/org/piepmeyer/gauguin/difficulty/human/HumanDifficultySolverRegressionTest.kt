package org.piepmeyer.gauguin.difficulty.human

import io.kotest.assertions.withClue
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.MergingCageGridCalculator
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant

@Ignored
class HumanDifficultySolverRegressionTest :
    FunSpec({
        // 10_000 of 4x4, random: 6 left unsolved

        test("2x4") {
            solveGrids(10_000, 2, 4) shouldBe Pair(0, 0)
        }

        test("3x4") {
            solveGrids(10_000, 3, 4) shouldBe Pair(0, 33)
        }

        test("4x4") {
            solveGrids(10_000, 4, 4) shouldBe Pair(0, 5)
        }

        test("4x4 with zeros") {
            solveGrids(10_000, 4, 4, GameOptionsVariant.createClassic().copy(digitSetting = DigitSetting.FIRST_DIGIT_ZERO)) shouldBe
                Pair(0, 85)
        }

        test("5x5") {
            solveGrids(10_000, 5, 5) shouldBe Pair(0, 23)
        }

        test("3x6") {
            solveGrids(1_000, 3, 6) shouldBe Pair(0, 70)
        }

        test("6x6") {
            solveGrids(1_000, 6, 6) shouldBe Pair(0, 21)
        }

        test("9x9") {
            solveGrids(100, 9, 9) shouldBe Pair(6, 11)
        }

        test("11x11") {
            solveGrids(10, 11, 11) shouldBe Pair(3, 1)
        }
    }) {
    companion object {
        suspend fun solveGrids(
            numberOfGrids: Int,
            width: Int,
            height: Int,
            options: GameOptionsVariant = GameOptionsVariant.createClassic(),
        ): Pair<Int, Int> {
            var unsolved = 0
            var solvedWithNishio = 0

            withClue("$width x $height, $numberOfGrids grids") {
                for (seed in 0..<numberOfGrids) {
                    val randomizer = SeedRandomizerMock(seed)

                    val calculator =
                        MergingCageGridCalculator(
                            GameVariant(
                                GridSize(width, height),
                                options,
                            ),
                            randomizer,
                            RandomPossibleDigitsShuffler(randomizer.random),
                        )

                    val grid = calculator.calculate()
                    grid.cells.forEach { it.possibles = grid.variant.possibleDigits }

                    val solver = HumanSolver(grid, true)

                    val solverResult = solver.solveAndCalculateDifficulty()

                    if (solverResult.usedNishio && solverResult.success) {
                        solvedWithNishio++
                    }

                    if (!solverResult.success) {
                        if (grid.numberOfMistakes() != 0) {
                            throw IllegalStateException("Found a grid with wrong values.")
                        }
                                /*grid.isActive = true
                                grid.startedToBePlayed = true
                                grid.description = "${grid.gridSize.width}x${grid.gridSize.height}-$seed"
                                val saveGame =
                                    SaveGame.createWithFile(
                                        File(
                                            SaveGame.SAVEGAME_NAME_PREFIX +
                                                "${grid.numberOfMistakes()}-${grid.gridSize.width}x${grid.gridSize.height}-$seed.yml",
                                        ),
                                    )

                                saveGame.save(grid)*/
                    }

                    print(".")

                    if (seed % 100 == 0) {
                        print(seed)
                    }

                    if (!solverResult.success) {
                        unsolved++
                    }
                }
            }

            return Pair(unsolved, solvedWithNishio)
        }
    }
}
