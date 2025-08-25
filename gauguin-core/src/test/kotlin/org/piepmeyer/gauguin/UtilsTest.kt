package org.piepmeyer.gauguin

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class UtilsTest :
    FunSpec({

        test("duration of zero shows minutes and seconds") {
            Utils.displayableGameDuration(0.minutes) shouldBe "00:00"
        }

        test("duration of 61 seconds shows 1:01") {
            Utils.displayableGameDuration(61.seconds) shouldBe "01:01"
        }

        test("duration less than one hour does not contain hour component") {
            Utils.displayableGameDuration(50.minutes) shouldBe "50:00"
        }

        test("duration with more than one hour does contain hour component") {
            Utils.displayableGameDuration(90.minutes) shouldBe "1:30:00"
        }

        test("duration with two-digit hours does contain two-digit hour component") {
            Utils.displayableGameDuration(23.hours) shouldBe "23:00:00"
        }
    })
