package org.piepmeyer.gauguin.game.save

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.math.roundToInt

class TestSaveGameAllSupportedVersions :
    FunSpec({
        listOf("1", "2", "3").forEach {
            test("reading saved grid file version $it") {
                val saveGameContent =
                    this::class.java
                        .getResource("/org/piepmeyer/gauguin/game/save/savegame_version_$it.yml")!!
                        .readText()

                val tempFile = tempfile()
                tempFile.writeText(saveGameContent)
                val loadedGrid = SaveGame.createWithFile(tempFile).restore()

                loadedGrid.shouldNotBeNull()

                loadedGrid.gridSize.width shouldBe 6
                loadedGrid.gridSize.height shouldBe 6

                loadedGrid.cells.first().userValue shouldBe null

                if (it == "2" || it == "3") {
                    assertSoftly {
                        loadedGrid.cells[26].possibles shouldBe listOf(2, 3)
                        loadedGrid.cells[32].userValue shouldBe 6
                        loadedGrid.cells[33].userValue shouldBe null
                        loadedGrid.difficulty.classicalRating!!.roundToInt() shouldBe 29
                        loadedGrid.difficulty.humanDifficulty shouldBe 852
                        loadedGrid.difficulty.solvedViaHumanDifficulty shouldBe true
                    }
                }
            }
        }
    })
