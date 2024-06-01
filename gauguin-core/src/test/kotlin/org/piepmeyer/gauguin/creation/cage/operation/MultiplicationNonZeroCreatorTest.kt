package org.piepmeyer.gauguin.creation.cage.operation

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class MultiplicationNonZeroCreatorTest : FunSpec({

    test("6x6, result 6, constraints matched") {
        val possibleNums =
            MultiplicationNonZeroCreator(
                cage = mockk { every { satisfiesConstraints(any()) } returns true },
                setOf(1, 2, 3, 4, 5, 6),
                6,
                2,
            ).create()

        assertSoftly {
            possibleNums.size shouldBe 4
            possibleNums shouldContain intArrayOf(1, 6)
            possibleNums shouldContain intArrayOf(2, 3)
            possibleNums shouldContain intArrayOf(3, 2)
            possibleNums shouldContain intArrayOf(6, 1)
        }
    }

    test("6x6, result 6, constraints matched sometimes") {
        val possibleNums =
            MultiplicationNonZeroCreator(
                mockk {
                    every { satisfiesConstraints(any()) } answers {
                        !firstArg<IntArray>().contentEquals(intArrayOf(2, 3))
                    }
                },
                setOf(1, 2, 3, 4, 5, 6),
                6,
                2,
            ).create()

        assertSoftly {
            possibleNums.size shouldBe 3
            possibleNums shouldContain intArrayOf(1, 6)
            possibleNums shouldContain intArrayOf(3, 2)
            possibleNums shouldContain intArrayOf(6, 1)
        }
    }

    test("8x8, result 24, constraints matched") {
        val possibleNums =
            MultiplicationNonZeroCreator(
                cage = mockk { every { satisfiesConstraints(any()) } returns true },
                setOf(1, 2, 3, 4, 5, 6, 7, 8),
                24,
                3,
            ).create()

        assertSoftly {
            possibleNums.size shouldBe 21

            possibleNums shouldContain intArrayOf(1, 4, 6)
            possibleNums shouldContain intArrayOf(1, 6, 4)
            possibleNums shouldContain intArrayOf(4, 1, 6)
            possibleNums shouldContain intArrayOf(4, 6, 1)
            possibleNums shouldContain intArrayOf(6, 1, 4)
            possibleNums shouldContain intArrayOf(6, 4, 1)

            possibleNums shouldContain intArrayOf(1, 3, 8)
            possibleNums shouldContain intArrayOf(1, 8, 3)
            possibleNums shouldContain intArrayOf(3, 1, 8)
            possibleNums shouldContain intArrayOf(3, 8, 1)
            possibleNums shouldContain intArrayOf(8, 1, 3)
            possibleNums shouldContain intArrayOf(8, 3, 1)

            possibleNums shouldContain intArrayOf(2, 2, 6)
            possibleNums shouldContain intArrayOf(2, 6, 2)
            possibleNums shouldContain intArrayOf(6, 2, 2)

            possibleNums shouldContain intArrayOf(2, 3, 4)
            possibleNums shouldContain intArrayOf(2, 4, 3)
            possibleNums shouldContain intArrayOf(3, 2, 4)
            possibleNums shouldContain intArrayOf(3, 4, 2)
            possibleNums shouldContain intArrayOf(4, 2, 3)
            possibleNums shouldContain intArrayOf(4, 3, 2)
        }
    }
})
