package org.piepmeyer.gauguin.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.piepmeyer.gauguin.creation.dlx.UniqueIndexSetsOfGivenLength

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class UniqueIndexSetsOfGivenLengthBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun test3x6() {
        benchmarkRule.measureRepeated {
            UniqueIndexSetsOfGivenLength(8, 3).calculateProduct()
        }
    }
}
