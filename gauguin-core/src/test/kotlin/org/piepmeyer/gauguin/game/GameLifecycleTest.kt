package org.piepmeyer.gauguin.game

import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.runs
import io.mockk.verify
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.mock.MockProvider
import org.koin.test.mock.declareMock
import org.piepmeyer.gauguin.preferences.ApplicationPreferences

class GameLifecycleTest : FunSpec(), KoinTest {
    init {
        MockProvider.register { mockkClass(it) }

        test("new grid gets pencils filled if option is enabled") {

            startKoin { }

            declareMock<ApplicationPreferences> {
                every { addPencilsAtStart() } returns true
                every { fillSingleCagesAtStart() } returns false
            }

            val game =
                declareMock<Game> {
                    every { grid.addPossiblesAtNewGame() } just runs
                }

            val lifecircle = GameLifecycle(mockk())

            lifecircle.prepareNewGrid()

            verify {
                game.grid.addPossiblesAtNewGame()
            }

            stopKoin()
        }

        test("new grid gets empty pencils if option is disabled") {
            startKoin { }

            declareMock<ApplicationPreferences> {
                every { addPencilsAtStart() } returns false
                every { fillSingleCagesAtStart() } returns false
            }

            val game =
                declareMock<Game> {
                    every { grid.addPossiblesAtNewGame() } just runs
                }

            val lifecircle = GameLifecycle(mockk())

            lifecircle.prepareNewGrid()

            verify(exactly = 0) {
                game.grid.addPossiblesAtNewGame()
            }

            stopKoin()
        }

        test("new grid gets all empty cages filled if option is enabled") {

            startKoin { }

            declareMock<ApplicationPreferences> {
                every { addPencilsAtStart() } returns false
                every { fillSingleCagesAtStart() } returns true
            }

            val game =
                declareMock<Game> {
                    every { fillSingleCagesInNewGrid() } just runs
                }

            val lifecircle = GameLifecycle(mockk())

            lifecircle.prepareNewGrid()

            verify {
                game.fillSingleCagesInNewGrid()
            }

            stopKoin()
        }

        test("new grid gets no empty cages filled if option is disabled") {
            startKoin { }

            declareMock<ApplicationPreferences> {
                every { addPencilsAtStart() } returns false
                every { fillSingleCagesAtStart() } returns false
            }

            val game =
                declareMock<Game> {
                    every { fillSingleCagesInNewGrid() } just runs
                }

            val lifecircle = GameLifecycle(mockk())

            lifecircle.prepareNewGrid()

            verify(exactly = 0) {
                game.fillSingleCagesInNewGrid()
            }

            stopKoin()
        }
    }
}
