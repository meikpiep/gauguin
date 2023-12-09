package org.piepmeyer.gauguin.creation.cage

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class TestGridCageType : FunSpec({

    test("SINGLE") {
        GridCageType.SINGLE.satisfiesConstraints(intArrayOf()) shouldBe true
    }

    test("DOUBLE_HORIZONTAL") {
        GridCageType.DOUBLE_HORIZONTAL.satisfiesConstraints(intArrayOf(2, 3)) shouldBe true
        GridCageType.DOUBLE_HORIZONTAL.satisfiesConstraints(intArrayOf(2, 2)) shouldBe false
    }

    test("DOUBLE_VERTICAL") {
        GridCageType.DOUBLE_VERTICAL.satisfiesConstraints(intArrayOf(2, 3)) shouldBe true
        GridCageType.DOUBLE_VERTICAL.satisfiesConstraints(intArrayOf(2, 2)) shouldBe false
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
        GridCageType.ANGLE_RIGHT_BOTTOM.satisfiesConstraints(intArrayOf(2, 3, 2)) shouldBe false
        GridCageType.ANGLE_RIGHT_BOTTOM.satisfiesConstraints(intArrayOf(2, 2, 3)) shouldBe false
        GridCageType.ANGLE_RIGHT_BOTTOM.satisfiesConstraints(intArrayOf(3, 2, 2)) shouldBe true
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
    ) { gridCageType ->
        // 0..2 in a row, 3 adjacent to 2
        gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 2)) shouldBe true
        gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 3)) shouldBe true
        gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 4)) shouldBe false
        gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4, 5)) shouldBe true

        gridCageType.satisfiesConstraints(intArrayOf(2, 3, 2, 4)) shouldBe false
        gridCageType.satisfiesConstraints(intArrayOf(2, 2, 3, 4)) shouldBe false
        gridCageType.satisfiesConstraints(intArrayOf(3, 2, 2, 4)) shouldBe false
        gridCageType.satisfiesConstraints(intArrayOf(2, 2, 2, 3)) shouldBe false
    }
})
