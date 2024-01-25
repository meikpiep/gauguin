package org.piepmeyer.gauguin.game

import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

class GameLifecycleTest : FunSpec({
    test("new grid gets pencils filled if option is enabled") {
        val preferences =
            mockk<ApplicationPreferences> {
                every { addPencilsAtStart() } returns true
                every { fillSingleCagesAtStart() } returns false
            }

        val game =
            mockk<Game> {
                every { grid.addPossiblesAtNewGame() } just runs
            }

        val lifecircle =
            GameLifecycle(
                mockk(),
                game,
                preferences,
            )

        lifecircle.prepareNewGrid()

        verify {
            game.grid.addPossiblesAtNewGame()
        }
    }

    test("new grid gets empty pencils if option is disabled") {
        val preferences =
            mockk<ApplicationPreferences> {
                every { addPencilsAtStart() } returns false
                every { fillSingleCagesAtStart() } returns false
            }

        val game =
            mockk<Game> {
                every { grid.addPossiblesAtNewGame() } just runs
            }

        val lifecircle =
            GameLifecycle(
                mockk(),
                game,
                preferences,
            )

        lifecircle.prepareNewGrid()

        verify(exactly = 0) {
            game.grid.addPossiblesAtNewGame()
        }
    }

    test("new grid gets all empty cages filled if option is enabled") {
        val preferences =
            mockk<ApplicationPreferences> {
                every { addPencilsAtStart() } returns false
                every { fillSingleCagesAtStart() } returns true
            }

        val game =
            mockk<Game> {
                every { fillSingleCagesInNewGrid() } just runs
            }

        val lifecircle =
            GameLifecycle(
                mockk(),
                game,
                preferences,
            )

        lifecircle.prepareNewGrid()

        verify {
            game.fillSingleCagesInNewGrid()
        }
    }

    test("new grid gets no empty cages filled if option is disabled") {
        val preferences =
            mockk<ApplicationPreferences> {
                every { addPencilsAtStart() } returns false
                every { fillSingleCagesAtStart() } returns false
            }

        val game =
            mockk<Game> {
                every { fillSingleCagesInNewGrid() } just runs
            }

        val lifecircle =
            GameLifecycle(
                mockk(),
                game,
                preferences,
            )

        lifecircle.prepareNewGrid()

        verify(exactly = 0) {
            game.fillSingleCagesInNewGrid()
        }
    }
})
