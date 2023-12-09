package org.piepmeyer.gauguin.creation.cage

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameOptionsVariant.Companion.createClassic
import org.piepmeyer.gauguin.options.GameVariant

class TestSubtractionCreator : FunSpec({
    test("allSubtractResults") {
        val variant =
            GameVariant(
                GridSize(4, 4),
                createClassic(),
            )

        val possibleNums = SubtractionCreator(variant, 2).create()

        possibleNums.size shouldBe 4

        possibleNums[0][0] shouldBe 1
        possibleNums[0][1] shouldBe 3
        possibleNums[1][0] shouldBe 2
        possibleNums[1][1] shouldBe 4
        possibleNums[2][0] shouldBe 3
        possibleNums[2][1] shouldBe 1
        possibleNums[3][0] shouldBe 4
        possibleNums[3][1] shouldBe 2
    }
})
