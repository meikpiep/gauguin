package org.piepmeyer.gauguin.creation.cage

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.piepmeyer.gauguin.grid.GridCage
import org.piepmeyer.gauguin.grid.GridCageAction
import org.piepmeyer.gauguin.grid.GridCell

class GridCageResultCalculatorTest : FunSpec({

    test("divide with 1 cell throws exception") {
        val cage = GridCage(0, true, GridCageAction.ACTION_DIVIDE, mockk())

        cage.addCell(createCellWithValue(2))

        shouldThrow<IllegalStateException> {
            GridCageResultCalculator(cage).calculateResultFromAction()
        }
    }

    test("divide with 3 cells throws exception") {
        val cage = GridCage(0, true, GridCageAction.ACTION_DIVIDE, mockk())

        cage.addCell(createCellWithValue(2))
        cage.addCell(createCellWithValue(2))
        cage.addCell(createCellWithValue(2))

        shouldThrow<IllegalStateException> {
            GridCageResultCalculator(cage).calculateResultFromAction()
        }
    }

    test("divide with 2 cells, cell sort a") {
        val cage = GridCage(0, true, GridCageAction.ACTION_DIVIDE, mockk())

        cage.addCell(createCellWithValue(10))
        cage.addCell(createCellWithValue(5))

        GridCageResultCalculator(cage).calculateResultFromAction() shouldBe 2
    }

    test("divide with 2 cells, cell sort b") {
        val cage = GridCage(0, true, GridCageAction.ACTION_DIVIDE, mockk())

        cage.addCell(createCellWithValue(5))
        cage.addCell(createCellWithValue(10))

        GridCageResultCalculator(cage).calculateResultFromAction() shouldBe 2
    }

    test("divide with one cell having value 0, cell sort a") {
        val cage = GridCage(0, true, GridCageAction.ACTION_DIVIDE, mockk())

        cage.addCell(createCellWithValue(0))
        cage.addCell(createCellWithValue(5))

        GridCageResultCalculator(cage).calculateResultFromAction() shouldBe 0
    }

    test("divide with one cell having value 0, cell sort b") {
        val cage = GridCage(0, true, GridCageAction.ACTION_DIVIDE, mockk())

        cage.addCell(createCellWithValue(5))
        cage.addCell(createCellWithValue(0))

        GridCageResultCalculator(cage).calculateResultFromAction() shouldBe 0
    }

    test("subtract with 1 cell throws exception") {
        val cage = GridCage(0, true, GridCageAction.ACTION_SUBTRACT, mockk())

        cage.addCell(createCellWithValue(2))

        shouldThrow<IllegalStateException> {
            GridCageResultCalculator(cage).calculateResultFromAction()
        }
    }

    test("subtract with 3 cells throws exception") {
        val cage = GridCage(0, true, GridCageAction.ACTION_SUBTRACT, mockk())

        cage.addCell(createCellWithValue(2))
        cage.addCell(createCellWithValue(2))
        cage.addCell(createCellWithValue(2))

        shouldThrow<IllegalStateException> {
            GridCageResultCalculator(cage).calculateResultFromAction()
        }
    }

    test("subtract with 2 cells, cell sort a") {
        val cage = GridCage(0, true, GridCageAction.ACTION_SUBTRACT, mockk())

        cage.addCell(createCellWithValue(10))
        cage.addCell(createCellWithValue(7))

        GridCageResultCalculator(cage).calculateResultFromAction() shouldBe 3
    }

    test("subtract with 2 cells, cell sort b") {
        val cage = GridCage(0, true, GridCageAction.ACTION_SUBTRACT, mockk())

        cage.addCell(createCellWithValue(7))
        cage.addCell(createCellWithValue(10))

        GridCageResultCalculator(cage).calculateResultFromAction() shouldBe 3
    }

    test("multiply with 2 cells") {
        val cage = GridCage(0, true, GridCageAction.ACTION_MULTIPLY, mockk())

        cage.addCell(createCellWithValue(2))
        cage.addCell(createCellWithValue(5))

        GridCageResultCalculator(cage).calculateResultFromAction() shouldBe 10
    }

    test("multiply with 4 cells") {
        val cage = GridCage(0, true, GridCageAction.ACTION_MULTIPLY, mockk())

        cage.addCell(createCellWithValue(1))
        cage.addCell(createCellWithValue(2))
        cage.addCell(createCellWithValue(5))
        cage.addCell(createCellWithValue(3))

        GridCageResultCalculator(cage).calculateResultFromAction() shouldBe 30
    }

    test("addition with 2 cells") {
        val cage = GridCage(0, true, GridCageAction.ACTION_ADD, mockk())

        cage.addCell(createCellWithValue(2))
        cage.addCell(createCellWithValue(5))

        GridCageResultCalculator(cage).calculateResultFromAction() shouldBe 7
    }

    test("addition with 4 cells") {
        val cage = GridCage(0, true, GridCageAction.ACTION_ADD, mockk())

        cage.addCell(createCellWithValue(1))
        cage.addCell(createCellWithValue(2))
        cage.addCell(createCellWithValue(5))
        cage.addCell(createCellWithValue(3))

        GridCageResultCalculator(cage).calculateResultFromAction() shouldBe 11
    }
})

fun createCellWithValue(cellValue: Int): GridCell {
    return mockk {
        every { cage = any() } just runs
        every { value } returns cellValue
    }
}
