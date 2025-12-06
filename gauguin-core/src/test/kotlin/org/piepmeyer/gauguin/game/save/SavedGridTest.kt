package org.piepmeyer.gauguin.game.save

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridCreatorIgnoringDifficulty
import org.piepmeyer.gauguin.creation.SeedRandomizerMock
import org.piepmeyer.gauguin.creation.ShufflerStub
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.undo.UndoStep

class SavedGridTest :
    FunSpec({

        test("From Grid to SavedGrid to Grid should get same grid core values") {
            val randomizer = SeedRandomizerMock(1)

            val grid =
                GridCreatorIgnoringDifficulty(
                    variant =
                        GameVariant(
                            GridSize(5, 5),
                            GameOptionsVariant.createClassic(),
                        ),
                    randomizer = randomizer,
                    shuffler = ShufflerStub(),
                ).createRandomizedGridWithCages()

            grid.undoSteps.add(UndoStep(grid.cells[4], 3, emptySet(), true))
            grid.undoSteps.add(UndoStep(grid.cells[3], null, setOf(1, 2), false))

            val savedGrid = SavedGrid.fromGrid(grid)
            val gridFromSavedGrid = savedGrid.toGrid()

            gridFromSavedGrid.toString() shouldBe grid.toString()
        }

        test("From Grid to SavedGrid to Grid should get same undo steps") {
            val randomizer = SeedRandomizerMock(1)

            val grid =
                GridCreatorIgnoringDifficulty(
                    variant =
                        GameVariant(
                            GridSize(5, 5),
                            GameOptionsVariant.createClassic(),
                        ),
                    randomizer = randomizer,
                    shuffler = ShufflerStub(),
                ).createRandomizedGridWithCages()

            grid.undoSteps.add(UndoStep(grid.cells[4], 3, emptySet(), true))
            grid.undoSteps.add(UndoStep(grid.cells[3], null, setOf(1, 2), false))

            val savedGrid = SavedGrid.fromGrid(grid)
            val gridFromSavedGrid = savedGrid.toGrid()

            gridFromSavedGrid.undoSteps shouldContainExactly
                listOf(
                    UndoStep(gridFromSavedGrid.cells[4], 3, emptySet(), true),
                    UndoStep(gridFromSavedGrid.cells[3], null, setOf(1, 2), false),
                )
        }
    })
