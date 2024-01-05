
import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import org.piepmeyer.gauguin.ui.grid.GridLayoutDetails

class TestAndroid : FunSpec({

    test("sample") {
        GridLayoutDetails(1.0f, mockk(relaxed = true))
    }
})
