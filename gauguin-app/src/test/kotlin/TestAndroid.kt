import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.options.NumeralSystem

class TestAndroid : FunSpec({

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
})
