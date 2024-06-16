package org.piepmeyer.gauguin.preferences

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class LegacyStatisticsManagerTest :
    FunSpec(
        {
            data class BestTimeTestData(
                val solveTime: Duration,
                val bestTime: Duration,
                val expectedNewBestTime: Boolean,
            )

            withData(
                BestTimeTestData(1.seconds, 0.seconds, true),
                BestTimeTestData(100.seconds, 0.seconds, true),
                BestTimeTestData(4.9.seconds, 5.seconds, true),
                BestTimeTestData(5.0.seconds, 5.seconds, false),
                BestTimeTestData(5.1.seconds, 5.seconds, false),
                BestTimeTestData(4.1.seconds, 5.2.seconds, true),
                BestTimeTestData(4.99.seconds, 5.2.seconds, true),
                BestTimeTestData(5.0.seconds, 5.2.seconds, false),
                BestTimeTestData(5.2.seconds, 5.2.seconds, false),
            ) { testData ->
                val statisticsManager = LegacyStatisticsManager(mockk())

                statisticsManager.isNewBestTime(testData.solveTime, testData.bestTime) shouldBe testData.expectedNewBestTime
            }
        },
    )
