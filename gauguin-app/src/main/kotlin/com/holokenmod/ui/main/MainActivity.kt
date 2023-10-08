/***************************************************************************
 * Copyright 2016 Adam Queler
 * HolokenMod - KenKen(tm) game developed for Android
 * (A modified version of Holoken 1.1.1 and 1.2 by Amanda Chow which was)
 * (a modified version of MathDoku 1.9 by Ben Buxton and Stephen Lee)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 */
package com.holokenmod.ui.main

import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import com.holokenmod.R
import com.holokenmod.StatisticsManager
import com.holokenmod.calculation.GridCalculationListener
import com.holokenmod.calculation.GridCalculationService
import com.holokenmod.databinding.ActivityMainBinding
import com.holokenmod.game.Game
import com.holokenmod.game.GameLifecycle
import com.holokenmod.game.GridCreationListener
import com.holokenmod.game.SaveGame.Companion.createWithFile
import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridSize
import com.holokenmod.options.*
import com.holokenmod.options.CurrentGameOptionsVariant.instance
import com.holokenmod.ui.ActivityUtils
import com.holokenmod.ui.MainDialogs
import com.holokenmod.ui.grid.GridCellSizeService
import nl.dionsegijn.konfetti.core.PartyFactory
import nl.dionsegijn.konfetti.core.emitter.Emitter
import org.koin.android.ext.android.inject
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.max

class MainActivity : AppCompatActivity(), GridCreationListener {
    private val game: Game by inject()
    private val gameLifecycle: GameLifecycle by inject()
    private val calculationService: GridCalculationService by inject()
    private val applicationPreferences: ApplicationPreferencesImpl by inject()
    private val activityUtils: ActivityUtils by inject()
    private val cellSizeService: GridCellSizeService by inject()

    private lateinit var topFragment: GameTopFragment

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomAppBarService: MainBottomAppBarService

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.MainScreenTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)
        applicationPreferences.loadGameVariant()

        gameLifecycle.setCoroutineScope(this.lifecycleScope)

        game.gridUI = binding.gridview
        binding.gridview.setOnLongClickListener {
            game.setValueOrPossiblesOnSelectedCell()
        }

        val ft = supportFragmentManager.beginTransaction()
        topFragment = GameTopFragment()
        ft.replace(R.id.keypadFrame, KeyPadFragment())
        ft.replace(R.id.gameTopFrame, topFragment)
        ft.commit()

        game.setRemovePencils(applicationPreferences.removePencils())

        cellSizeService.setCellSizeListener { cellSizePercent ->
            binding.gridview.setCellSizePercent(cellSizePercent)
            binding.gridview.forceLayout()
        }

        game.setSolvedHandler { gameSolved() }

        registerForContextMenu(binding.gridview)

        bottomAppBarService = MainBottomAppBarService(this, binding)
        bottomAppBarService.initialize()

        MainNavigationViewService(this, binding).initialize()

        calculationService.addListener(createGridCalculationListener())
        loadApplicationPreferences()

        freshGridWasCreated()

        bottomAppBarService.updateAppBarState()

        if (applicationPreferences.newUserCheck()) {
            MainDialogs(this).openHelpDialog()
        }
    }

    private fun createGridCalculationListener(): GridCalculationListener {
        return object : GridCalculationListener {
            override fun startingCurrentGridCalculation() {
                runOnUiThread {
                    binding.pendingCurrentGridCalculation.visibility = View.VISIBLE
                    binding.pendingNextGridCalculation.visibility = View.INVISIBLE
                    binding.gridview.visibility = View.INVISIBLE
                    binding.ferrisWheelView.visibility = View.VISIBLE
                    binding.ferrisWheelView.startAnimation()
                }
            }

            override fun currentGridCalculated(currentGrid: Grid) {
                runOnUiThread {
                    binding.pendingCurrentGridCalculation.visibility = View.INVISIBLE
                    binding.pendingNextGridCalculation.visibility = View.INVISIBLE
                }
                showAndStartGame(currentGrid)
            }

            override fun startingNextGridCalculation() {
                runOnUiThread {
                    binding.pendingCurrentGridCalculation.visibility = View.INVISIBLE
                    binding.pendingNextGridCalculation.visibility = View.VISIBLE
                }
            }

            override fun nextGridCalculated(currentGrid: Grid) {
                runOnUiThread {
                    binding.pendingCurrentGridCalculation.visibility = View.INVISIBLE
                    binding.pendingNextGridCalculation.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun gameSolved() {
        gameLifecycle.gameSolved()

        showProgress(getString(R.string.puzzle_solved))

        bottomAppBarService.updateAppBarState()

        binding.hintOrNewGame.hide()
        binding.hintOrNewGame.show()

        val statisticsManager = createStatisticsManager()
        val recordTime = statisticsManager.storeStatisticsAfterFinishedGame()
        recordTime?.let { showProgress("${getString(R.string.puzzle_record_time)} $it") }
        statisticsManager.storeStreak(true)
        topFragment.setGameTime(grid.playTime)

        val konfettiView = binding.konfettiView
        val emitterConfig = Emitter(15L, TimeUnit.SECONDS).perSecond(150)

        val colors = listOf(
            MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorPrimary),
            MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorOnPrimary),
            MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorSecondary),
            MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorOnSecondary),
            MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorTertiary),
            MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorOnTertiary))

        val party = PartyFactory(emitterConfig)
            .angle(270)
            .spread(90)
            .setSpeedBetween(1f, 5f)
            .timeToLive(3000L)
            .position(0.0, 0.0, 1.0, 0.0)
            .colors(colors)
            .build()
        konfettiView.start(party)
    }

    private fun createStatisticsManager(): StatisticsManager {
        return StatisticsManager(this, grid)
    }

    private fun showAndStartGame(currentGrid: Grid) {
        runOnUiThread {
            binding.konfettiView.reset()

            binding.gridview.grid = currentGrid
            updateGameObject(currentGrid)
            TransitionManager.beginDelayedTransition(binding.container, Fade(Fade.OUT))
            startFreshGrid(true)
            binding.gridview.visibility = View.VISIBLE
            binding.gridview.reCreate()
            binding.gridview.invalidate()
            binding.ferrisWheelView.visibility = View.INVISIBLE
            binding.ferrisWheelView.stopAnimation()
            TransitionManager.endTransitions(binding.container)
        }
    }

    fun cheatedOnGame() {
        makeToast(R.string.toast_cheated)
        createStatisticsManager().storeStreak(false)
    }

    private val grid: Grid
        get() = binding.gridview.grid

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
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
        Log.d("HoloKen", "Loading game: $filename")

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
        if (grid.isActive) {
            binding.gridview.requestFocus()
            binding.gridview.invalidate()
            gameLifecycle.resumeGame()
        }
        super.onResume()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
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
        insetsChanged()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (window != null) {
            insets = WindowInsetsCompat.toWindowInsetsCompat(
                window.decorView
                    .rootWindowInsets
            )
        }
        insetsChanged()
    }

    private fun insetsChanged() {
        insets?.let {
            runOnUiThread {
                if (binding.mainTopAreaStart == null)
                    return@runOnUiThread

                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.mainConstraintLayout)
                constraintSet.setGuidelineBegin(binding.mainTopAreaStart!!.id, rightEdgeOfCutOutArea(it))
                constraintSet.setGuidelineEnd(
                    binding.mainTopAreaEnd!!.id,
                    it.getInsets(WindowInsetsCompat.Type.statusBars()).right
                )
                val topAreaBottom = max(
                    (0.25 * this@MainActivity.resources.displayMetrics.xdpi).toInt(),
                    it.getInsets(WindowInsetsCompat.Type.statusBars()).bottom
                )
                constraintSet.setGuidelineBegin(binding.mainTopAreaBottom!!.id, topAreaBottom)
                constraintSet.applyTo(binding.mainConstraintLayout)
                binding.mainConstraintLayout.requestLayout()
            }
        }
    }

    private fun rightEdgeOfCutOutArea(insets: WindowInsetsCompat): Int {
        val cutout = insets.displayCutout
        return if (cutout == null || cutout.boundingRects.isEmpty()) {
            0
        } else cutout.boundingRects[0].right
    }

    fun createNewGame() {
        MainDialogs(this).newGameGridDialog()
    }

    private fun postNewGame() {
        if (grid.isActive) {
            createStatisticsManager().storeStreak(false)
        }

        val gridSize = GridSize(
            applicationPreferences.gridWidth,
            applicationPreferences.gridHeigth
        )

        val variant = GameVariant(
            gridSize,
            instance().copy()
        )
        if (calculationService.hasCalculatedNextGrid(variant)) {
            val grid = calculationService.consumeNextGrid()
            grid.isActive = true
            showAndStartGame(grid)
            val t = Thread { calculationService.calculateNextGrid() }
            t.name = "PreviewCalculatorFromMainNext-" + variant.width + "x" + variant.height
            t.start()
        } else {
            val grid = Grid(variant)
            binding.gridview.grid = grid
            val t = Thread {
                calculationService.calculateCurrentAndNextGrids(variant)
            }
            t.name = "PreviewCalculatorFromMainNonNext-" + variant.width + "x" + variant.height
            t.start()
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
            grid.isActive = true
            createStatisticsManager().storeStatisticsAfterNewGame()
            gameLifecycle.startNewGrid()
        }
    }

    private fun showGrid() {
        val grid = game.grid

        startFreshGrid(false)

        game.updateGrid(grid)

        gameLifecycle.showGrid()

        binding.gridview.invalidate()
        calculationService.setVariant(
            GameVariant(
                binding.gridview.grid.gridSize,
                instance().copy()
            )
        )
        //GridCalculationService.getInstance().calculateNextGrid();
    }

    fun checkProgressOrStartNewGame() {
        if (grid.isSolved) {
            postNewGame()
        } else {
            checkProgress()
        }
    }

    private fun checkProgress() {
        val mistakes = grid.numberOfMistakes(applicationPreferences.showDupedDigits())
        val filled = grid.numberOfFilledCells()
        val text = (resources.getQuantityString(
            R.plurals.toast_mistakes,
            mistakes, mistakes
        )
                + " / " +
                resources.getQuantityString(
                    R.plurals.toast_filled,
                    filled, filled
                ))
        val duration: Int = if (mistakes == 0) {
            1500
        } else {
            4000
        }
        Snackbar.make(binding.hintOrNewGame, text, duration)
            .setAnchorView(binding.hintOrNewGame)
            .setAction("Undo") {
                game.restoreUndo()
                binding.gridview.invalidate()
                checkProgressOrStartNewGame()
            }
            .show()
    }

    private fun showProgress(string: String) {
        Snackbar.make(binding.hintOrNewGame, string, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.hintOrNewGame)
            .show()
    }


    fun gameSaved() {
        Snackbar.make(binding.hintOrNewGame, "Game saved sucessfully.", Snackbar.LENGTH_LONG)
            .setAnchorView(binding.hintOrNewGame)
            .show()
    }

    private fun makeToast(resId: Int) {
        Snackbar.make(binding.hintOrNewGame, resId, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.hintOrNewGame)
            .show()
    }

    companion object {
        private var insets: WindowInsetsCompat? = null
    }

    override fun freshGridWasCreated() {
        showGrid()
    }
}