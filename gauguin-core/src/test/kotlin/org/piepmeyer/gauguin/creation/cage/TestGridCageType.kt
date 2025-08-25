package org.piepmeyer.gauguin.creation.cage

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class TestGridCageType :
    FunSpec({

        test("SINGLE") {
            GridCageType.SINGLE.satisfiesConstraints(intArrayOf()) shouldBe true
        }

        withData(
            GridCageType.DOUBLE_HORIZONTAL,
            GridCageType.DOUBLE_VERTICAL,
        ) { gridCageType ->
            gridCageType.satisfiesConstraints(intArrayOf(2, 3)) shouldBe true
            gridCageType.satisfiesConstraints(intArrayOf(2, 2)) shouldBe false
        }

        withData(
            GridCageType.TRIPLE_HORIZONTAL,
            GridCageType.TRIPLE_VERTICAL,
        ) { gridCageType ->
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4)) shouldBe true

            gridCageType.satisfiesConstraints(intArrayOf(2, 2, 3)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 2)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(3, 2, 2)) shouldBe false
        }

        withData(
            GridCageType.FOUR_VERTICAL,
            GridCageType.FOUR_HORIZONTAL,
        ) { gridCageType ->
            gridCageType.satisfiesConstraints(intArrayOf(1, 2, 3, 4)) shouldBe true

            gridCageType.satisfiesConstraints(intArrayOf(1, 1, 2, 3)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(1, 2, 1, 3)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(1, 2, 3, 1)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(1, 2, 2, 3)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(1, 2, 3, 2)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(1, 2, 3, 3)) shouldBe false
        }

        withData(
            GridCageType.ANGLE_LEFT_BOTTOM,
            GridCageType.ANGLE_RIGHT_TOP,
            GridCageType.ANGLE_LEFT_TOP,
        ) { gridCageType ->
            // second number is point at the edge
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4)) shouldBe true
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 2)) shouldBe true

            gridCageType.satisfiesConstraints(intArrayOf(2, 2, 3)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(3, 2, 2)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(2, 2, 2)) shouldBe false
        }

        test("ANGLE_RIGHT_BOTTOM") {
            // first number is point at the edge
            GridCageType.ANGLE_RIGHT_BOTTOM.satisfiesConstraints(intArrayOf(2, 3, 4)) shouldBe true
            GridCageType.ANGLE_RIGHT_BOTTOM.satisfiesConstraints(intArrayOf(3, 2, 2)) shouldBe true

            GridCageType.ANGLE_RIGHT_BOTTOM.satisfiesConstraints(intArrayOf(2, 3, 2)) shouldBe false
            GridCageType.ANGLE_RIGHT_BOTTOM.satisfiesConstraints(intArrayOf(2, 2, 3)) shouldBe false
            GridCageType.ANGLE_RIGHT_BOTTOM.satisfiesConstraints(intArrayOf(2, 2, 2)) shouldBe false
        }

        test("SQUARE") {
            GridCageType.SQUARE.satisfiesConstraints(intArrayOf(2, 3, 4, 5)) shouldBe true
            GridCageType.SQUARE.satisfiesConstraints(intArrayOf(2, 3, 4, 2)) shouldBe true
            GridCageType.SQUARE.satisfiesConstraints(intArrayOf(3, 2, 2, 4)) shouldBe true

            GridCageType.SQUARE.satisfiesConstraints(intArrayOf(2, 2, 3, 4)) shouldBe false
            GridCageType.SQUARE.satisfiesConstraints(intArrayOf(2, 3, 2, 4)) shouldBe false
            GridCageType.SQUARE.satisfiesConstraints(intArrayOf(3, 2, 4, 2)) shouldBe false
        }

        withData(
            GridCageType.L_VERTICAL_SHORT_RIGHT_TOP,
            GridCageType.L_HORIZONTAL_SHORT_LEFT_BOTTOM,
        ) { gridCageType ->
            // 0..2 in a row, 3 adjacent to 0
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 3)) shouldBe true
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 4)) shouldBe true
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 5)) shouldBe true

            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 2)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 2, 4)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(2, 2, 3, 4)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(3, 2, 2, 4)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(2, 2, 2, 3)) shouldBe false
        }

        withData(
            GridCageType.L_VERTICAL_SHORT_LEFT_BOTTOM,
            GridCageType.L_HORIZONTAL_SHORT_RIGHT_BOTTOM,
            GridCageType.L_VERTICAL_SHORT_RIGHT_BOTTOM,
        ) { gridCageType ->
            // 0..2 in a row, 3 adjacent to 2
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 2)) shouldBe true
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 3)) shouldBe true
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 5)) shouldBe true

            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 2, 4)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(2, 2, 3, 4)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(3, 2, 2, 4)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(2, 2, 2, 3)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 4)) shouldBe false
        }

        test("L_HORIZONTAL_SHORT_RIGHT_TOP") {
            // 1..3 in a row, 0 adjacent to 3
            GridCageType.L_HORIZONTAL_SHORT_RIGHT_TOP.should {
                it.satisfiesConstraints(intArrayOf(2, 3, 4, 5)) shouldBe true
                it.satisfiesConstraints(intArrayOf(2, 2, 3, 4)) shouldBe true
                it.satisfiesConstraints(intArrayOf(2, 3, 2, 4)) shouldBe true

                it.satisfiesConstraints(intArrayOf(2, 3, 4, 2)) shouldBe false
                it.satisfiesConstraints(intArrayOf(2, 3, 3, 4)) shouldBe false
                it.satisfiesConstraints(intArrayOf(2, 3, 4, 3)) shouldBe false
                it.satisfiesConstraints(intArrayOf(2, 3, 4, 4)) shouldBe false
            }
        }

        withData(
            GridCageType.L_HORIZONTAL_SHORT_LEFT_TOP,
            GridCageType.L_VERTICAL_SHORT_LEFT_TOP,
        ) { gridCageType ->
            // 1..3 in a row, 0 adjacent to 1
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 5)) shouldBe true
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 2)) shouldBe true
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 2, 4)) shouldBe true
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 2)) shouldBe true

            gridCageType.satisfiesConstraints(intArrayOf(2, 2, 3, 4)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 3, 4)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 3)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 4)) shouldBe false
        }

        withData(
            GridCageType.TETRIS_HORIZONTAL_LEFT_TOP,
            GridCageType.TETRIS_VERTICAL_LEFT_TOP,
        ) { gridCageType ->
            // 0 to 1, 1 to 2, 2 to 3
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 5)) shouldBe true
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 2, 4)) shouldBe true
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 2)) shouldBe true
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 3)) shouldBe true

            gridCageType.satisfiesConstraints(intArrayOf(2, 2, 3, 4)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 3, 4)) shouldBe false
            gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 4)) shouldBe false
        }

        test("TETRIS_HORIZONTAL_RIGHT_TOP") {
            // 0 to 1, 0 to 3, 2 to 3
            GridCageType.TETRIS_HORIZONTAL_RIGHT_TOP.should {
                it.satisfiesConstraints(intArrayOf(2, 3, 4, 5)) shouldBe true
                it.satisfiesConstraints(intArrayOf(2, 3, 2, 4)) shouldBe true
                it.satisfiesConstraints(intArrayOf(2, 3, 3, 4)) shouldBe true
                it.satisfiesConstraints(intArrayOf(2, 3, 4, 3)) shouldBe true

                it.satisfiesConstraints(intArrayOf(2, 2, 3, 4)) shouldBe false
                it.satisfiesConstraints(intArrayOf(2, 3, 4, 2)) shouldBe false
                it.satisfiesConstraints(intArrayOf(2, 3, 4, 4)) shouldBe false
            }
        }

        test("classic types") {
            GridCageType.classicCageTypes().size shouldBe 14
        }
    })
