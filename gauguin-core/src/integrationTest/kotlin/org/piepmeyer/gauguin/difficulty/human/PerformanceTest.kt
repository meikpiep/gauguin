package org.piepmeyer.gauguin.difficulty.human

import jdk.jfr.ContentType
import org.apache.http.entity.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import us.abstracta.jmeter.javadsl.JmeterDsl.*
import us.abstracta.jmeter.javadsl.core.TestPlanStats
import java.io.IOException
import java.time.Duration

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
                        .post("{\"name\": \"test\"}", ContentType.APPLICATION_JSON),
                ), // this is just to log details of each request stats
                jtlWriter("target/jtls"),
            ).run()
        assertThat(stats.overall().sampleTimePercentile99()).isLessThan(Duration.ofSeconds(5))
    }
}
