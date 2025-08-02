package org.piepmeyer.gauguin.creation.cage.operation

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.piepmeyer.gauguin.options.GameVariant

class MultiplicationCreatorTest :
    FunSpec({

        test("non-zero, 6x6, result 6, constraints matched") {
            val possibleNums =
                MultiplicationCreator(
                    cage = mockk { every { satisfiesConstraints(any()) } returns true },
                    mockk<GameVariant> { every { possibleNonZeroDigits } returns setOf(1, 2, 3, 4, 5, 6) },
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

        test("non-zero, 6x6, result 6, constraints matched sometimes") {
            val possibleNums =
                MultiplicationCreator(
                    cage =
                        mockk {
                            every { satisfiesConstraints(any()) } answers {
                                !firstArg<IntArray>().contentEquals(intArrayOf(2, 3))
                            }
                        },
                    mockk<GameVariant> { every { possibleNonZeroDigits } returns setOf(1, 2, 3, 4, 5, 6) },
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

        test("non-zero, 8x8, result 24, constraints matched") {
            val possibleNums =
                MultiplicationCreator(
                    cage = mockk { every { satisfiesConstraints(any()) } returns true },
                    mockk<GameVariant> { every { possibleNonZeroDigits } returns setOf(1, 2, 3, 4, 5, 6, 7, 8) },
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

        test("zero two cells, all constraints matching") {
            val possibleNums =
                MultiplicationCreator(
                    cage = mockk { every { satisfiesConstraints(any()) } returns true },
                    mockk<GameVariant> { every { possibleDigits } returns setOf(0, 1, 2, 3) },
                    0,
                    2,
                ).create()

            assertSoftly {
                possibleNums.size shouldBe 7
                possibleNums shouldContain intArrayOf(0, 0)
                possibleNums shouldContain intArrayOf(0, 1)
                possibleNums shouldContain intArrayOf(0, 2)
                possibleNums shouldContain intArrayOf(0, 3)
                possibleNums shouldContain intArrayOf(1, 0)
                possibleNums shouldContain intArrayOf(2, 0)
                possibleNums shouldContain intArrayOf(3, 0)
            }
        }

        test("zero two cells, some constraints not matching") {
            val possibleNums =
                MultiplicationCreator(
                    cage =
                        mockk {
                            every { satisfiesConstraints(any()) } answers {
                                firstArg<IntArray>()[0] != 2 && firstArg<IntArray>()[1] != 1
                            }
                        },
                    mockk<GameVariant> { every { possibleDigits } returns setOf(0, 1, 2, 3) },
                    0,
                    2,
                ).create()

            assertSoftly {
                possibleNums.size shouldBe 5
                possibleNums shouldContain intArrayOf(0, 0)
                possibleNums shouldContain intArrayOf(0, 2)
                possibleNums shouldContain intArrayOf(0, 3)
                possibleNums shouldContain intArrayOf(1, 0)
                possibleNums shouldContain intArrayOf(3, 0)
            }
        }
    })
