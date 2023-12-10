package org.piepmeyer.gauguin.creation.cage.operation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.DigitSetting
import org.piepmeyer.gauguin.options.GameOptionsVariant
import org.piepmeyer.gauguin.options.GameVariant

class TestDivideCreator : FunSpec({
    test("allDivideResultsWithoutZero") {
        val variant =
            GameVariant(
                GridSize(4, 4),
                GameOptionsVariant.createClassic(),
            )

        val possibleNums = DivideCreator(variant, 2).create()

        possibleNums.size shouldBe 4

        possibleNums[0][0] shouldBe 2
        possibleNums[0][1] shouldBe 1
        possibleNums[1][0] shouldBe 1
        possibleNums[1][1] shouldBe 2
        possibleNums[2][0] shouldBe 4
        possibleNums[2][1] shouldBe 2
        possibleNums[3][0] shouldBe 2
        possibleNums[3][1] shouldBe 4
    }

    test("allDivideResultsWithZero") {
        val variant =
            GameVariant(
                GridSize(4, 4),
                GameOptionsVariant.createClassic(DigitSetting.FIRST_DIGIT_ZERO),
            )

        val possibleNums = DivideCreator(variant, 0).create()

        possibleNums.size shouldBe 6

        possibleNums[0][0] shouldBe 1
        possibleNums[0][1] shouldBe 0
        possibleNums[1][0] shouldBe 0
        possibleNums[1][1] shouldBe 1
        possibleNums[2][0] shouldBe 2
        possibleNums[2][1] shouldBe 0
        possibleNums[3][0] shouldBe 0
        possibleNums[3][1] shouldBe 2
        possibleNums[4][0] shouldBe 3
        possibleNums[4][1] shouldBe 0
        possibleNums[5][0] shouldBe 0
        possibleNums[5][1] shouldBe 3
    }
})
