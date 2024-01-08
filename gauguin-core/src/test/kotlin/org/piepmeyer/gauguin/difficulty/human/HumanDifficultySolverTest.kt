package org.piepmeyer.gauguin.difficulty.human

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.grid.GridCageAction

class HumanDifficultySolverTest : FunSpec({
    test("difficult 3x3 grid gets solved via human difficulty resolver") {

        val grid =
            GridBuilder(3)
                .addCage(5, GridCageAction.ACTION_ADD, GridCageType.ANGLE_RIGHT_BOTTOM, 0)
                .addSingleCage(2, 2)
                .addCage(1, GridCageAction.ACTION_SUBTRACT, GridCageType.DOUBLE_HORIZONTAL, 4)
                .addCage(6, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_HORIZONTAL, 6)
                .addValueRow(3, 1, 2)
                .addValueRow(1, 2, 3)
                .addValueRow(2, 3, 1)
                .createGrid()

        val solver = HumanSolver(grid)

        solver.solve()

        grid.isSolved() shouldBe true
    }
})
