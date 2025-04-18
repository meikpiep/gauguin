package org.piepmeyer.gauguin.game.save

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.math.roundToInt

class TestSaveGameAllSupportedVersions :
    FunSpec({
        listOf("1", "2").forEach {
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

                if (it == "2") {
                    assertSoftly {
                        loadedGrid.difficulty.classicalRating!!.roundToInt() shouldBe 29
                        loadedGrid.difficulty.humanDifficulty shouldBe 852
                        loadedGrid.difficulty.solvedViaHumanDifficulty shouldBe true
                    }
                }
            }
        }
    })
