package org.piepmeyer.gauguin.creation.cage.operation

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant

class AdditionCreatorTest :
    FunSpec({
        test("4x4 3 cells in cage") {
            val variant =
                GameVariant(
                    GridSize(4, 4),
                    GameOptionsVariant.createClassic(),
                )

            val possibleNums =
                AdditionCreator(
                    cage =
                        mockk {
                            every { satisfiesConstraints(any()) } answers {
                                true
                            }
                        },
                    variant,
                    6,
                    3,
                ).create()

            assertSoftly {
                possibleNums.size shouldBe 10
                possibleNums shouldContain intArrayOf(1, 1, 4)
                possibleNums shouldContain intArrayOf(1, 2, 3)
                possibleNums shouldContain intArrayOf(1, 3, 2)
                possibleNums shouldContain intArrayOf(1, 4, 1)
                possibleNums shouldContain intArrayOf(2, 1, 3)
                possibleNums shouldContain intArrayOf(2, 2, 2)
                possibleNums shouldContain intArrayOf(2, 3, 1)
                possibleNums shouldContain intArrayOf(3, 1, 2)
                possibleNums shouldContain intArrayOf(3, 2, 1)
                possibleNums shouldContain intArrayOf(4, 1, 1)
            }
        }
    })
