package com.holokenmod.creation.cage

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
        GridCageType.TRIPLE_VERTICAL
    ) { gridCageType ->
        gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4)) shouldBe true
        gridCageType.satisfiesConstraints(intArrayOf(2, 2, 3)) shouldBe false
        gridCageType.satisfiesConstraints(intArrayOf(2, 3, 2)) shouldBe false
        gridCageType.satisfiesConstraints(intArrayOf(3, 2, 2)) shouldBe false
    }

    withData(
        GridCageType.ANGLE_ONE,
        GridCageType.ANGLE_TWO,
        GridCageType.ANGLE_THREE,
        GridCageType.ANGLE_FOUR
    ) { gridCageType ->
        gridCageType.satisfiesConstraints(intArrayOf(2, 3, 4)) shouldBe true
        gridCageType.satisfiesConstraints(intArrayOf(2, 3, 2)) shouldBe true
        gridCageType.satisfiesConstraints(intArrayOf(2, 2, 3)) shouldBe false
        gridCageType.satisfiesConstraints(intArrayOf(3, 2, 2)) shouldBe false
        gridCageType.satisfiesConstraints(intArrayOf(2, 2, 2)) shouldBe false
    }

    test("SQUARE") {
        GridCageType.SQUARE.satisfiesConstraints(intArrayOf(2, 3, 4, 5)) shouldBe true
        GridCageType.SQUARE.satisfiesConstraints(intArrayOf(2, 3, 4, 2)) shouldBe true
        GridCageType.SQUARE.satisfiesConstraints(intArrayOf(3, 2, 2, 4)) shouldBe true
        GridCageType.SQUARE.satisfiesConstraints(intArrayOf(2, 2, 3, 4)) shouldBe false
        GridCageType.SQUARE.satisfiesConstraints(intArrayOf(2, 3, 2, 4)) shouldBe false
        GridCageType.SQUARE.satisfiesConstraints(intArrayOf(3, 2, 4, 2)) shouldBe false
    }
})
