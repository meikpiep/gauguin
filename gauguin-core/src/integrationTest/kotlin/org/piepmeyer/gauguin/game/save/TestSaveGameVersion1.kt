package org.piepmeyer.gauguin.game.save

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe

class TestSaveGameVersion1 :
    FunSpec({
        test("reading saved grid file version 1") {
            val saveGameFile =
                this::class.java
                    .getResource("/org/piepmeyer/gauguin/game/save/savegame_version_1.yml")!!
                    .readText()

            SaveGame.restore(saveGameFile) shouldNotBe null
        }
    })
