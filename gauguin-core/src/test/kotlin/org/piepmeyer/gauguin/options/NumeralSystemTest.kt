package org.piepmeyer.gauguin.options

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class NumeralSystemTest :
    FunSpec({

        context("binary") {
            withData(
                listOf(
                    Pair(-4, "-100"),
                    Pair(-2, "-10"),
                    Pair(-1, "-1"),
                    Pair(0, "0"),
                    Pair(1, "1"),
                    Pair(2, "10"),
                    Pair(3, "11"),
                    Pair(4, "100"),
                ),
            ) { (value, expectedValue) ->
                NumeralSystem.Binary.displayableString(value) shouldBe expectedValue
            }
        }

        context("quarternary") {
            withData(
                listOf(
                    Pair(-5, "-11"),
                    Pair(-1, "-1"),
                    Pair(0, "0"),
                    Pair(1, "1"),
                    Pair(3, "3"),
                    Pair(4, "10"),
                    Pair(5, "11"),
                ),
            ) { (value, expectedValue) ->
                NumeralSystem.Quaternary.displayableString(value) shouldBe expectedValue
            }
        }

        context("octal") {
            withData(
                listOf(
                    Pair(-9, "-11"),
                    Pair(-5, "-5"),
                    Pair(0, "0"),
                    Pair(1, "1"),
                    Pair(3, "3"),
                    Pair(7, "7"),
                    Pair(8, "10"),
                    Pair(9, "11"),
                ),
            ) { (value, expectedValue) ->
                NumeralSystem.Octal.displayableString(value) shouldBe expectedValue
            }
        }

        context("decimal") {
            withData(
                listOf(
                    Pair(-5, "-5"),
                    Pair(0, "0"),
                    Pair(1, "1"),
                    Pair(9, "9"),
                    Pair(10, "10"),
                    Pair(11, "11"),
                ),
            ) { (value, expectedValue) ->
                NumeralSystem.Decimal.displayableString(value) shouldBe expectedValue
            }
        }

        context("hexadecimal") {
            withData(
                listOf(
                    Pair(-15, "-F"),
                    Pair(-5, "-5"),
                    Pair(0, "0"),
                    Pair(1, "1"),
                    Pair(9, "9"),
                    Pair(10, "A"),
                    Pair(15, "F"),
                    Pair(16, "10"),
                ),
            ) { (value, expectedValue) ->
                NumeralSystem.Hexadecimal.displayableString(value) shouldBe expectedValue
            }
        }
    })
