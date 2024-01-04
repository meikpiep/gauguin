package org.piepmeyer.gauguin.grid

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GridSizeTest : FunSpec({

    test("square grid") {
        val gridsize = GridSize(5, 5)

        gridsize.height shouldBe 5
        gridsize.width shouldBe 5
        gridsize.surfaceArea shouldBe 25
        gridsize.isSquare shouldBe true
        gridsize.amountOfNumbers shouldBe 5
        gridsize.largestSide() shouldBe 5
        gridsize.smallestSide() shouldBe 5
    }

    test("rectangular grid") {
        val gridsize = GridSize(10, 5)

        gridsize.height shouldBe 5
        gridsize.width shouldBe 10
        gridsize.surfaceArea shouldBe 50
        gridsize.isSquare shouldBe false
        gridsize.amountOfNumbers shouldBe 10
        gridsize.largestSide() shouldBe 10
        gridsize.smallestSide() shouldBe 5
    }
})
