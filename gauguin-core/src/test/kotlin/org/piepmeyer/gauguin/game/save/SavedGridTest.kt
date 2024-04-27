package org.piepmeyer.gauguin.game.save

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridCreator
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.creation.ShufflerStub
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant

private val logger = KotlinLogging.logger {}

class SavedGridTest : FunSpec({

    test("From Grid to SavedGrid to Grid should get same grid") {
        val randomizer = SeedRandomizerMock(1)

        val grid =
            GridCreator(
                variant =
                    GameVariant(
                        GridSize(5, 5),
                        GameOptionsVariant.createClassic(),
                    ),
                randomizer = randomizer,
                shuffler = ShufflerStub(),
            ).createRandomizedGridWithCages()

        val savedGrid = SavedGrid.fromGrid(grid)

        val gridFromSavedGrid = savedGrid.toGrid()

        gridFromSavedGrid.toString() shouldBe grid.toString()

        logger.info { grid.toString() }
    }
})
