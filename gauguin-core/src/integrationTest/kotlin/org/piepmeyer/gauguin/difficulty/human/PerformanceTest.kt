package org.piepmeyer.gauguin.difficulty.human

import io.kotest.matchers.comparables.shouldBeLessThan
import org.junit.jupiter.api.Test
import us.abstracta.jmeter.javadsl.JmeterDsl.*
import us.abstracta.jmeter.javadsl.core.TestPlanStats
import java.io.IOException
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class PerformanceTest {
    @Test
    @Throws(IOException::class)
    fun testPerformance() {
        val stats: TestPlanStats =
            testPlan(
                threadGroup(
                    10,
                    10,
                    httpSampler("http://my.service")
                        .post("{\"name\": \"test\"}", org.apache.http.entity.ContentType.APPLICATION_JSON),
                ), // this is just to log details of each request stats
                jtlWriter("target/jtls"),
            ).run()

        stats.overall().sampleTimePercentile99() shouldBeLessThan 5.seconds.toJavaDuration()
    }
}
