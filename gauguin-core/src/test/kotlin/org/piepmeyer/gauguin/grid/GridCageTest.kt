package org.piepmeyer.gauguin.grid

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class GridCageTest :
    FunSpec({

        data class CageMathTestData(
            val userValues: List<Int>,
            val cageResult: Int,
            val expectedCorrect: Boolean,
        )

        context("isDivideMathsCorrect with number of cells != 2 returns false") {
            withData(0, 1, 3, 4) { numberOfCells ->
                val cage = GridCage(0, true, mockk(), mockk())

                cage.result = 1

                cage.cells =
                    List(numberOfCells) {
                        mockk {
                            every { userValue } returns 1
                        }
                    }

                cage.isDivideMathsCorrect() shouldBe false
            }
        }

        context("isDivideMathsCorrect with two cells") {
            withData(
                nameFn = { "${it.userValues}__${it.cageResult}__${it.expectedCorrect}" },
                // exactly two values, result != 0
                // result 7
                CageMathTestData(listOf(1, 7), 7, true),
                CageMathTestData(listOf(1, 6), 7, false),
                CageMathTestData(listOf(1, 8), 7, false),
                CageMathTestData(listOf(0, 7), 7, false),
                CageMathTestData(listOf(2, 7), 7, false),
                CageMathTestData(listOf(7, 7), 7, false),
                // result 3
                CageMathTestData(listOf(1, 3), 3, true),
                CageMathTestData(listOf(2, 6), 3, true),
                CageMathTestData(listOf(3, 9), 3, true),
                CageMathTestData(listOf(1, 9), 3, false),
                CageMathTestData(listOf(1, 6), 3, false),
                CageMathTestData(listOf(6, 9), 3, false),
                // result 0
                CageMathTestData(listOf(0, 0), 0, false),
                CageMathTestData(listOf(0, 1), 0, true),
                CageMathTestData(listOf(0, 2), 0, true),
            ) { data: CageMathTestData ->

                withData(
                    nameFn = { if (it) "original order" else "reversed order" },
                    first = false,
                    second = true,
                ) { reversed ->
                    val userValues =
                        if (reversed) {
                            data.userValues.reversed()
                        } else {
                            data.userValues
                        }

                    testDivideMathsCorrect(userValues, data.cageResult, data.expectedCorrect)
                }
            }
        }
    })

fun testDivideMathsCorrect(
    userValues: List<Int>,
    cageResult: Int,
    expectedCorrect: Boolean,
) {
    val cage = GridCage(0, true, mockk(), mockk())

    cage.result = cageResult
    cage.cells =
        listOf(
            mockk {
                every { userValue } returns userValues[0]
            },
            mockk {
                every { userValue } returns userValues[1]
            },
        )

    cage.isDivideMathsCorrect() shouldBe expectedCorrect
}
