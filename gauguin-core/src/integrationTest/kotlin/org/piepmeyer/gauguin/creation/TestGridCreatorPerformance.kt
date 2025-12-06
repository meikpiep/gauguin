package org.piepmeyer.gauguin.creation

import io.kotest.core.spec.style.FunSpec
import org.piepmeyer.gauguin.creation.dlx.DLX
import org.piepmeyer.gauguin.creation.dlx.MathDokuDLX
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant

class TestGridCreatorPerformance :
    FunSpec({
        for (seed in 0..299) {
            xtest("seed performance-DLX-$seed") {
                val randomizer = SeedRandomizerMock(seed)

                val variant =
                    GameVariant(
                        GridSize(10, 10),
                        GameOptionsVariant.createClassic(),
                    )

                val grid =
                    GridCreatorIgnoringDifficulty(variant, randomizer, RandomPossibleDigitsShuffler(randomizer.random))
                        .createRandomizedGridWithCages()

                println(MathDokuDLX(grid).solve(DLX.SolveType.MULTIPLE))
            }
        }
    })
