package org.piepmeyer.gauguin.difficulty.human

import io.kotest.common.runBlocking
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.piepmeyer.gauguin.creation.RandomCageGridCalculator
import org.piepmeyer.gauguin.creation.RandomPossibleDigitsShuffler
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import us.abstracta.jmeter.javadsl.JmeterDsl.*
import us.abstracta.jmeter.javadsl.core.TestPlanStats
import java.io.IOException
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class PerformanceTest {
    @Test
    @Throws(IOException::class)
    fun testPerformance() {
        val stats: TestPlanStats =
            testPlan(
                threadGroup(
                    10,
                    10,
                    jsr223Sampler { v ->
                        val randomizer = SeedRandomizerMock(1)

                        val calculator =
                            RandomCageGridCalculator(
                                GameVariant(
                                    GridSize(9, 9),
                                    GameOptionsVariant.createClassic(),
                                ),
                                randomizer,
                                RandomPossibleDigitsShuffler(randomizer.random),
                            )

                        val grid =
                            runBlocking {
                                calculator.calculate()
                            }

                        grid.cells.forEach { it.possibles = grid.variant.possibleDigits }

                        val solver = HumanSolver(grid)

                        val solverResult = solver.solveAndCalculateDifficulty()

                        println(grid.toString())

                        if (!grid.isSolved()) {
                            if (grid.numberOfMistakes() != 0) {
                                throw IllegalStateException("Found a grid with wrong values.")
                            }
                        }

                        grid.isSolved() shouldBe true

                        solverResult.difficulty shouldBeGreaterThan 0
                    },
                ), // this is just to log details of each request stats
                jtlWriter("target/jtls"),
            ).run()

        stats.overall().sampleTimePercentile99() shouldBeLessThan 10.seconds.toJavaDuration()
    }
}
