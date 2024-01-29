package org.piepmeyer.gauguin.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import nl.dionsegijn.konfetti.core.PartyFactory
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.calculation.GridCalculationListener
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.databinding.ActivityMainBinding
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.game.GridCreationListener
import org.piepmeyer.gauguin.game.save.SaveGame.Companion.createWithFile
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.preferences.StatisticsManager
import org.piepmeyer.gauguin.ui.ActivityUtils
import org.piepmeyer.gauguin.ui.MainDialogs
import org.piepmeyer.gauguin.ui.grid.GridCellSizeService
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), GridCreationListener {
    private val game: Game by inject()
    private val gameLifecycle: GameLifecycle by inject()
    private val statisticsManager: StatisticsManager by inject()
    private val calculationService: GridCalculationService by inject()
    private val applicationPreferences: ApplicationPreferences by inject()
    private val activityUtils: ActivityUtils by inject()
    private val cellSizeService: GridCellSizeService by inject()

    private lateinit var topFragment: GameTopFragment

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomAppBarService: MainBottomAppBarService

    private lateinit var specialListener: OnSharedPreferenceChangeListener

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)

        gameLifecycle.setCoroutineScope(this.lifecycleScope)

        game.gridUI = binding.gridview
        binding.gridview.setOnLongClickListener {
            game.longClickOnSelectedCell()
        }

        val ft = supportFragmentManager.beginTransaction()
        topFragment = GameTopFragment()
        ft.replace(R.id.keypadFrame, KeyPadFragment())
        ft.replace(R.id.fastFinishingModeFrame, FastFinishingModeFragment())
        ft.replace(R.id.gameSolvedFrame, GameSolvedFragment())
        ft.replace(R.id.gameTopFrame, topFragment)
        ft.commit()

        cellSizeService.setCellSizeListener {
            binding.gridview.invalidate()
            binding.gridview.forceLayout()
            binding.gridview.invalidate()
        }

        game.addGameSolvedHandler { reveal -> gameSolved(reveal) }

        registerForContextMenu(binding.gridview)

        bottomAppBarService = MainBottomAppBarService(this, binding)
        bottomAppBarService.initialize()

        MainNavigationViewService(this, binding).initialize()

        calculationService.addListener(createGridCalculationListener())
        loadApplicationPreferences()

        specialListener =
            OnSharedPreferenceChangeListener { _: SharedPreferences, key: String? ->
                if (key == "theme" || key == "maximumCellSize") {
                    this.recreate()
                }
            }

        val preferences = PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        preferences.registerOnSharedPreferenceChangeListener(specialListener)

        freshGridWasCreated()

        bottomAppBarService.updateAppBarState()

        MainDialogs(this).openNewUserHelpDialog()
    }

    private fun createGridCalculationListener(): GridCalculationListener {
        return object : GridCalculationListener {
            override fun startingCurrentGridCalculation() {
                runOnUiThread {
                    binding.gridview.visibility = View.INVISIBLE
                    binding.ferrisWheelView.visibility = View.VISIBLE
                    binding.ferrisWheelView.startAnimation()
                }
            }

            override fun currentGridCalculated(currentGrid: Grid) {
                showAndStartGame(currentGrid)
            }

            override fun startingNextGridCalculation() {
                runOnUiThread {
                    binding.pendingNextGridCalculation.visibility = View.VISIBLE
                }
            }

            override fun nextGridCalculated(currentGrid: Grid) {
                runOnUiThread {
                    binding.pendingNextGridCalculation.visibility = View.INVISIBLE
                }
            }

            override fun pushGridToMainActivity(grid: Grid) {
                grid.isActive = true
                binding.gridview.grid = grid
                binding.gridview.reCreate()
                binding.gridview.invalidate()
            }
        }
    }

    private fun gameSolved(reveal: Boolean) {
        gameLifecycle.gameSolved()

        bottomAppBarService.updateAppBarState()

        statisticsManager.storeStreak(!reveal)
        topFragment.setGameTime(game.grid.playTime)

        if (!reveal) {
            val konfettiView = binding.konfettiView

            val emitterConfig = Emitter(8L, TimeUnit.SECONDS).perSecond(150)

            val colors =
                listOf(
                    MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorPrimary),
                    MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorOnPrimary),
                    MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorSecondary),
                    MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorOnSecondary),
                    MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorTertiary),
                    MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorOnTertiary),
                )

            val party =
                PartyFactory(emitterConfig)
                    .angle(270)
                    .spread(90)
                    .setSpeedBetween(1f, 5f)
                    .timeToLive(3000L)
                    .position(0.0, 0.0, 1.0, 0.0)
                    .colors(colors)
                    .build()
            konfettiView.start(party)
        }
    }

    private fun showAndStartGame(
        currentGrid: Grid,
        startedFromMainActivity: Boolean = false,
    ) {
        runOnUiThread {
            if (startedFromMainActivity) {
                binding.konfettiView.stopGracefully()
            } else {
                binding.konfettiView.reset()
            }

            binding.gridview.grid = currentGrid

            updateGameObject(currentGrid)
            startFreshGrid(true)

            binding.gridview.reCreate()
            binding.gridview.invalidate()
            binding.gridview.visibility = View.VISIBLE

            binding.ferrisWheelView.visibility = View.INVISIBLE
            binding.ferrisWheelView.stopAnimation()
        }
    }

    fun cheatedOnGame() {
        statisticsManager.storeStreak(false)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }
        if (resultCode == 99) {
            postNewGame()
            return
        }
        if (requestCode != 7 || resultCode != RESULT_OK) {
            return
        }
        val filename = data.extras!!.getString("filename")!!

        val saver = createWithFile(File(filename))

        saver.restore()?.let {
            game.updateGrid(it)
            gameLifecycle.gameWasLoaded()
            showGrid()
        }
    }

    public override fun onPause() {
        gameLifecycle.pauseGame()

        super.onPause()
    }

    public override fun onResume() {
        loadApplicationPreferences()
        if (game.grid.isActive) {
            binding.gridview.requestFocus()
            binding.gridview.invalidate()
            gameLifecycle.resumeGame()
        }
        super.onResume()
    }

    override fun onKeyDown(
        keyCode: Int,
        event: KeyEvent,
    ): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK && binding.gridview.isSelectorShown) {
            binding.gridview.requestFocus()
            binding.gridview.isSelectorShown = false
            binding.gridview.invalidate()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun loadApplicationPreferences() {
        activityUtils.configureNightMode()
        activityUtils.configureKeepScreenOn(this)
        activityUtils.configureFullscreen(this)

        binding.gridview.updateTheme()
    }

    fun createNewGame() {
        MainDialogs(this).newGameGridDialog()
    }

    fun postNewGame(startedFromMainActivityWithSameVariant: Boolean = false) {
        if (game.grid.isActive && game.grid.startedToBePlayed) {
            statisticsManager.storeStreak(false)
        }

        val variant =
            if (startedFromMainActivityWithSameVariant) {
                game.grid.variant
            } else {
                GameVariant(
                    GridSize(
                        applicationPreferences.gridWidth,
                        applicationPreferences.gridHeigth,
                    ),
                    applicationPreferences.gameVariant,
                )
            }

        if (calculationService.hasCalculatedNextGrid(variant)) {
            val grid = calculationService.consumeNextGrid()
            grid.isActive = true
            showAndStartGame(grid, startedFromMainActivityWithSameVariant)

            calculationService.calculateNextGrid(lifecycleScope)
        } else {
            calculationService.calculateCurrentAndNextGrids(variant, lifecycleScope)
        }
    }

    private fun updateGameObject(newGrid: Grid) {
        game.updateGrid(newGrid)
    }

    fun startFreshGrid(newGame: Boolean) {
        game.clearUndoList()
        binding.gridview.updateTheme()

        bottomAppBarService.updateAppBarState()

        if (newGame) {
            game.grid.isActive = true
            statisticsManager.storeStatisticsAfterNewGame(game.grid)
            gameLifecycle.startNewGrid()
        }
    }

    private fun showGrid() {
        val grid = game.grid

        startFreshGrid(false)

        game.updateGrid(grid)

        gameLifecycle.showGrid()

        binding.gridview.invalidate()
        calculationService.variant =
            GameVariant(
                binding.gridview.grid.gridSize,
                applicationPreferences.gameVariant.copy(),
            )
        // calculationService.calculateNextGrid(lifecycleScope);
    }

    fun checkProgress() {
        if (game.grid.isSolved()) {
            return
        }

        val mistakes = game.grid.numberOfMistakes()
        val filled = game.grid.numberOfFilledCells()
        val text = (
            resources.getQuantityString(
                R.plurals.toast_mistakes,
                mistakes, mistakes,
            ) +
                " / " +
                resources.getQuantityString(
                    R.plurals.toast_filled,
                    filled, filled,
                )
        )
        val duration: Int =
            if (mistakes == 0) {
                1500
            } else {
                4000
            }

        val snackbar =
            Snackbar.make(binding.root, text, duration)

        val params = snackbar.view.layoutParams as ViewGroup.MarginLayoutParams

        val startMarginOfBottomAppBar =
            (binding.mainBottomAppBar.layoutParams as ViewGroup.MarginLayoutParams)
                .marginStart

        params.setMargins(
            params.leftMargin + startMarginOfBottomAppBar,
            params.topMargin,
            params.rightMargin,
            params.bottomMargin,
        )

        snackbar.view.layoutParams = params

        if (mistakes > 0 && game.undoManager.isUndoPossible()) {
            snackbar.setAction(resources.getText(R.string.hint_as_toast_undo_last_step)) {
                game.undoOneStep()
                checkProgress()
            }
        }

        snackbar.show()
    }

    fun gameSaved() {
        Snackbar.make(binding.root, resources.getText(R.string.main_activity_current_game_saved), Snackbar.LENGTH_LONG)
            .show()
    }

    override fun freshGridWasCreated() {
        showGrid()
    }
}
