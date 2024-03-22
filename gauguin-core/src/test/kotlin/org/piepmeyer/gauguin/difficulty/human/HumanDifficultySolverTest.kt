package org.piepmeyer.gauguin.difficulty.human

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridCalculator
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.game.save.SaveGame
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import java.io.File

class HumanDifficultySolverTest : FunSpec({
    for (seed in 0..99) {
        withClue("seed $seed") {
            test("seed random 6x6 grid should be solved") {
                val randomizer = SeedRandomizerMock(seed)

                val calculator =
                    GridCalculator(
                        GameVariant(
                            GridSize(6, 6),
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
                    val saveGame = SaveGame.createWithFile(File(SaveGame.SAVEGAME_NAME_PREFIX + "6x6-$seed.yml"))

                    saveGame.save(grid)
                }

                grid.isSolved() shouldBe true

                solverResult.difficulty shouldBeGreaterThan 0
            }
        }
    }
})
