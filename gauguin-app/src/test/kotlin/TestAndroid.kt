
import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import org.piepmeyer.gauguin.ui.grid.GridLayoutDetails

class TestAndroid :
    FunSpec({

        test("sample") {
            GridLayoutDetails(Pair(1.0f, 1.0f), mockk(relaxed = true))
        }
    })
