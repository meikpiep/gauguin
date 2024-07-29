package org.piepmeyer.gauguin.difficulty.human

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.piepmeyer.gauguin.game.save.SaveGame
import java.io.File
import java.nio.file.Files
import java.util.stream.Collectors
import kotlin.io.path.isDirectory
import kotlin.io.path.name

class HumanDifficultyBalanceTest :
    FunSpec({
        test("seed random 6x6 grid should be solved") {
            val savedGames =
                Files
                    .list(File("src/test/resources/4x4").toPath())
                    .collect(Collectors.toList())
                    .filter { !it.isDirectory() }

            val namesToGrids =
                savedGames
                    .associateWith {
                        val grid = SaveGame.createWithFile(it.toFile()).restore()!!
                        grid.clearUserValues()
                        grid.addPossiblesAtNewGame()

                        grid
                    }.mapKeys { it.key.name }

            val namesToDifficulties =
                namesToGrids.mapValues {
                    val result = HumanSolver(it.value).solveAndCalculateDifficulty()

                    withClue(it.key) {
                        result.success shouldBe true
                    }

                    result.difficulty
                }

            namesToDifficulties.entries
                .sortedBy { it.value }
                .forEach {
                    println("${it.value} -> ${it.key}")
                }
        }
    })
