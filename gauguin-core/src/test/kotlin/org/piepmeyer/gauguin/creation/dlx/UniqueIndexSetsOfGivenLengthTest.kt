package org.piepmeyer.gauguin.creation.dlx

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly

class UniqueIndexSetsOfGivenLengthTest :
    FunSpec({

        test("one copy returns input values") {
            val product = UniqueIndexSetsOfGivenLength(2, 1).calculateProduct()

            product.shouldContainExactly(
                intArrayOf(0),
                intArrayOf(1),
                intArrayOf(2),
            )
        }

        test("two copies of three elements") {
            val product = UniqueIndexSetsOfGivenLength(2, 2).calculateProduct()

            product.shouldContainExactly(
                intArrayOf(0, 1),
                intArrayOf(0, 2),
                intArrayOf(1, 2),
            )
        }

        test("three copies of three elements") {
            val values = 0..2

            val product = UniqueIndexSetsOfGivenLength(2, 3).calculateProduct()

            product.shouldContainExactly(
                intArrayOf(0, 1, 2),
            )
        }

        test("three copies of four elements") {
            val product = UniqueIndexSetsOfGivenLength(3, 3).calculateProduct()

            product.shouldContainExactly(
                intArrayOf(0, 1, 2),
                intArrayOf(0, 1, 3),
                intArrayOf(0, 2, 3),
                intArrayOf(1, 2, 3),
            )
        }
    })
