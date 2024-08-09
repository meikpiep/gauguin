package org.piepmeyer.gauguin.difficulty.human

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.MergingCageGridCalculator
import org.piepmeyer.gauguin.creation.RandomCageGridCalculator
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant

class HumanDifficultySolverHandpickedTest :
    FunSpec({
        test("6x6 grid should be solved") {
            // original difficulty 27, seems unsolvable by hand for meikpiep
            val calculator =
                GridBuilder(6, 6)
                    .addCage(20, GridCageAction.ACTION_ADD, GridCageType.TETRIS_HORIZONTAL_LEFT_TOP, 0)
                    .addCage(11, GridCageAction.ACTION_ADD, GridCageType.TETRIS_HORIZONTAL_LEFT_TOP, 2)
                    .addCage(2, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_HORIZONTAL, 4)
                    .addCage(11, GridCageAction.ACTION_ADD, GridCageType.FOUR_VERTICAL, 6)
                    .addCage(144, GridCageAction.ACTION_MULTIPLY, GridCageType.TETRIS_VERTICAL_RIGHT_TOP, 11)
                    .addCage(11, GridCageAction.ACTION_ADD, GridCageType.TRIPLE_HORIZONTAL, 13)
                    .addCage(24, GridCageAction.ACTION_MULTIPLY, GridCageType.L_HORIZONTAL_SHORT_LEFT_BOTTOM, 19)
                    .addCage(150, GridCageAction.ACTION_MULTIPLY, GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM, 23)
                    .addCage(4, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_VERTICAL, 26)
                    .addCage(15, GridCageAction.ACTION_ADD, GridCageType.ANGLE_RIGHT_BOTTOM, 27)
                    .addCage(4, GridCageAction.ACTION_DIVIDE, GridCageType.DOUBLE_HORIZONTAL, 30)

            val grid = calculator.createGrid()
            grid.cells.forEach { it.possibles = grid.variant.possibleDigits }

            val solver = HumanSolver(grid)

            println(grid.toString())

            val solverResult = solver.solveAndCalculateDifficulty()

            println(grid.toString())
            println(solverResult)

            solverResult.difficulty shouldBeGreaterThan 0
        }

        test("seed random grid should be solved") {
            val randomizer = SeedRandomizerMock(9184)

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

            grid.isSolved() shouldBe true
        }

        test("merging algo 4x4 wrong solution") {
            val randomizer = SeedRandomizerMock(6096)

            val calculator =
                MergingCageGridCalculator(
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

            grid.isSolved() shouldBe true
        }
    })
