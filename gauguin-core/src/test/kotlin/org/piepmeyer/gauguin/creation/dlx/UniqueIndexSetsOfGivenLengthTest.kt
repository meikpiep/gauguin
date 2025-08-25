package org.piepmeyer.gauguin.creation.dlx

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly

class UniqueIndexSetsOfGivenLengthTest :
    FunSpec({

        test("one copy returns input values") {
            val values = listOf(1, 2, 3)

            val product = UniqueIndexSetsOfGivenLength(values, 1).calculateProduct()

            product.shouldContainExactly(
                setOf(1),
                setOf(2),
                setOf(3),
            )
        }

        test("two copies of three elements") {
            val values = listOf(1, 2, 3)

            val product = UniqueIndexSetsOfGivenLength(values, 2).calculateProduct()

            product.shouldContainExactly(
                setOf(1, 2),
                setOf(1, 3),
                setOf(2, 3),
            )
        }

        test("three copies of three elements") {
            val values = listOf(1, 2, 3)

            val product = UniqueIndexSetsOfGivenLength(values, 3).calculateProduct()

            product.shouldContainExactly(
                setOf(setOf(1, 2, 3)),
            )
        }

        test("three copies of four elements") {
            val values = listOf(1, 2, 3, 4)

            val product = UniqueIndexSetsOfGivenLength(values, 3).calculateProduct()

            product.shouldContainExactly(
                setOf(1, 2, 3),
                setOf(1, 2, 4),
                setOf(1, 3, 4),
                setOf(2, 3, 4),
            )
        }
    })
