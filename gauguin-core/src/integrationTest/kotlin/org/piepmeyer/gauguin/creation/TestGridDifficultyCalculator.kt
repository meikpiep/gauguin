package org.piepmeyer.gauguin.creation

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant.Companion.createClassic
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.SingleCageUsage

class TestGridDifficultyCalculator : FunSpec({
    test("difficulty").config(invocations = 3) {
        val creator = GridCreator(
            GameVariant(
                GridSize(9, 9),
                createClassic()
            )
        )
        val grid = creator.createRandomizedGridWithCages()
        println(GridDifficultyCalculator(grid).calculate())
    }

    xtest("calculateValues") {
        val difficulties = mutableListOf<Double>()

        val options = createClassic()
        options.singleCageUsage = SingleCageUsage.NO_SINGLE_CAGES
        val creator = GridCalculator(
            GameVariant(
                GridSize(9, 9),
                options
            )
        )

        kotlinx.coroutines.runBlocking(Dispatchers.Default) {
            for (i in 0..999) {
                launch {
                    val grid = creator.calculate()
                    difficulties.add(GridDifficultyCalculator(grid).calculate())
                    print(".")
                }
            }
        }

        difficulties.sort()
        println(difficulties.size)
        println("50: " + difficulties[49])
        println("333: " + difficulties[332])
        println("667: " + difficulties[666])
        println("950: " + difficulties[949])
    }
})
