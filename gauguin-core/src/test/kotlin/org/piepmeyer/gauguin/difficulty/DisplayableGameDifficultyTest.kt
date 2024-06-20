package org.piepmeyer.gauguin.difficulty

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class DisplayableGameDifficultyTest :
    FunSpec({

        test("without rating") {
            val difficulty = DisplayableGameDifficulty(null)

            difficulty.displayableDifficultyValue(20.0) shouldBe 20.toBigDecimal()
            difficulty.displayableDifficultyValue(30.0) shouldBe 30.toBigDecimal()
            difficulty.displayableDifficultyValue(30.4) shouldBe 30.toBigDecimal()
            difficulty.displayableDifficultyValue(30.5) shouldBe 31.toBigDecimal()
            difficulty.displayableDifficultyValue(30.6) shouldBe 31.toBigDecimal()
            difficulty.displayableDifficultyValue(19.0) shouldBe 19.0.toBigDecimal()
            difficulty.displayableDifficultyValue(19.5) shouldBe 19.5.toBigDecimal()
            difficulty.displayableDifficultyValue(5.5) shouldBe 5.5.toBigDecimal()
            difficulty.displayableDifficultyValue(5.0) shouldBe 5.0.toBigDecimal()
        }

        test("with small rating") {
            val rating =
                mockk<GameDifficultyRating> {
                    every { thresholdExtreme } returns 19.9
                }

            val difficulty = DisplayableGameDifficulty(rating)

            difficulty.displayableDifficultyValue(20.0) shouldBe 20.0.toBigDecimal()
            difficulty.displayableDifficultyValue(30.0) shouldBe 30.0.toBigDecimal()
            difficulty.displayableDifficultyValue(30.4) shouldBe 30.4.toBigDecimal()
            difficulty.displayableDifficultyValue(30.5) shouldBe 30.5.toBigDecimal()
            difficulty.displayableDifficultyValue(30.6) shouldBe 30.6.toBigDecimal()
            difficulty.displayableDifficultyValue(19.0) shouldBe 19.0.toBigDecimal()
            difficulty.displayableDifficultyValue(19.5) shouldBe 19.5.toBigDecimal()
            difficulty.displayableDifficultyValue(5.5) shouldBe 5.5.toBigDecimal()
            difficulty.displayableDifficultyValue(5.0) shouldBe 5.0.toBigDecimal()
        }

        test("with large rating") {
            val rating =
                mockk<GameDifficultyRating> {
                    every { thresholdExtreme } returns 20.0
                }

            val difficulty = DisplayableGameDifficulty(rating)

            difficulty.displayableDifficultyValue(20.0) shouldBe 20.toBigDecimal()
            difficulty.displayableDifficultyValue(30.0) shouldBe 30.toBigDecimal()
            difficulty.displayableDifficultyValue(30.4) shouldBe 30.toBigDecimal()
            difficulty.displayableDifficultyValue(30.5) shouldBe 31.toBigDecimal()
            difficulty.displayableDifficultyValue(30.6) shouldBe 31.toBigDecimal()
            difficulty.displayableDifficultyValue(19.0) shouldBe 19.toBigDecimal()
            difficulty.displayableDifficultyValue(19.5) shouldBe 20.toBigDecimal()
            difficulty.displayableDifficultyValue(5.5) shouldBe 6.toBigDecimal()
        }
    })
