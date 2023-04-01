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

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.color.MaterialColors
import com.google.android.material.navigation.NavigationView
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.holokenmod.R
import com.holokenmod.StatisticsManager
import com.holokenmod.Theme
import com.holokenmod.Utils.convertTimetoStr
import com.holokenmod.calculation.GridCalculationListener
import com.holokenmod.calculation.GridCalculationService
import com.holokenmod.databinding.ActivityMainBinding
import com.holokenmod.game.Game
import com.holokenmod.game.SaveGame
import com.holokenmod.game.SaveGame.Companion.createWithDirectory
import com.holokenmod.game.SaveGame.Companion.createWithFile
import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridSize
import com.holokenmod.grid.GridSize.Companion.create
import com.holokenmod.grid.GridView
import com.holokenmod.options.*
import com.holokenmod.options.CurrentGameOptionsVariant.instance
import com.holokenmod.options.GameOptionsVariant.Companion.createClassic
import com.holokenmod.ui.MainDialogs
import com.holokenmod.ui.grid.GridCellSizeListener
import com.holokenmod.ui.grid.GridCellSizeService
import com.holokenmod.undo.UndoListener
import com.holokenmod.undo.UndoManager
import nl.dionsegijn.konfetti.core.PartyFactory
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private val mTimerHandler = Handler(Looper.getMainLooper())
    private var starttime: Long = 0

    //runs without timer be reposting self
    private val playTimer: Runnable = object : Runnable {
        override fun run() {
            val millis = System.currentTimeMillis() - starttime
            topFragment!!.setGameTime(convertTimetoStr(millis))
            mTimerHandler.postDelayed(this, UPDATE_RATE.toLong())
        }
    }
    var game = Game(Grid(
        GameVariant(
            GridSize(9, 9),
            createClassic(DigitSetting.FIRST_DIGIT_ZERO)
        )),
        UndoManager(object : UndoListener {
            override fun undoStateChanged(undoPossible: Boolean) {

            }
        }),
        object : GridView {
            override fun requestFocus() = false

            override fun invalidate() {}
        }
    )

    private var keyPadFragment: KeyPadFragment? = null
    private var topFragment: GameTopFragment? = null
    private var undoButton: View? = null
    private var keypadFrameHorizontalBias = 0f
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("MissingInflatedId")
    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.MainScreenTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ApplicationPreferences.instance.setPreferenceManager(
            PreferenceManager.getDefaultSharedPreferences(this)
        )
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)
        ApplicationPreferences.instance.loadGameVariant()
        undoButton = findViewById(R.id.undo)
        val undoListener =
            object : UndoListener {
                override fun undoStateChanged(undoPossible: Boolean) {
                    undoButton!!.isEnabled = undoPossible
                }
            }
        val undoList = UndoManager(undoListener)

        //TODO echte Grid-Instanz nutzen
        game = Game(Grid(
            GameVariant(
                GridSize(9, 9),
                createClassic(DigitSetting.FIRST_DIGIT_ZERO)
            )),
            undoList,
            binding.gridview
        )
        undoButton!!.isEnabled = false
        val eraserButton = findViewById<View>(R.id.eraser)

        val ft = supportFragmentManager.beginTransaction()
        keyPadFragment = KeyPadFragment()
        topFragment = GameTopFragment()
        ft.replace(R.id.keypadFrame, keyPadFragment!!)
        ft.replace(R.id.gameTopFrame, topFragment!!)
        ft.commit()

        binding.gridview.initializeWithGame(game)
        GridCellSizeService.instance
            .setCellSizeListener(object : GridCellSizeListener {
                override fun cellSizeChanged(cellSizePercent: Int) {
                    binding.gridview.setCellSizePercent(cellSizePercent)
                    binding.gridview.forceLayout()
                }
            })

        game.setSolvedHandler { gameSolved() }

        registerForContextMenu(binding.gridview)

        binding.hint.setOnClickListener { checkProgress() }
        undoButton!!.setOnClickListener { game.undoOneStep() }
        eraserButton.setOnClickListener { game.eraseSelectedCell() }

        val appBar = findViewById<BottomAppBar>(R.id.mainBottomAppBar)
        val navigationView = findViewById<NavigationView>(R.id.mainNavigationView)
        binding.container.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        navigationView.setNavigationItemSelectedListener(MainNavigationItemSelectedListener(this))

        val gridScaleSlider =
            navigationView.getHeaderView(0).findViewById<Slider>(R.id.gridScaleSlider)
        gridScaleSlider.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, fromUser: Boolean ->
            if (fromUser) {
                GridCellSizeService.instance.cellSizePercent = value.roundToInt()
            }
        })

        GridCellSizeService.instance.cellSizePercent = GridCellSizeService.instance.cellSizePercent //TODO
        if (appBar != null) {
            appBar.setOnMenuItemClickListener { menuItem: MenuItem -> appBarSelected(menuItem) }
            appBar.setNavigationOnClickListener { binding.container.open() }
        }
        GridCalculationService.instance.addListener(createGridCalculationListener())
        loadApplicationPreferences()

        if (ApplicationPreferences.instance.newUserCheck()) {
            MainDialogs(this, game).openHelpDialog()
        } else {
            val saver = createWithDirectory(this.filesDir)
            restoreSaveGame(saver)
        }
        println("onCreate")
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

    private fun appBarSelected(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        if (itemId == R.id.hint) {
            checkProgress()
        } else if (itemId == R.id.undo) {
            game.clearLastModified()
            game.restoreUndo()
            binding.gridview.invalidate()
        } else if (itemId == R.id.eraser) {
            game.eraseSelectedCell()
        } else if (itemId == R.id.menu_show_mistakes) {
            game.markInvalidChoices()
            cheatedOnGame()
        } else if (itemId == R.id.menu_reveal_cell) {
            if (game.revealSelectedCell()) {
                cheatedOnGame()
            }
        } else if (itemId == R.id.menu_reveal_cage) {
            if (game.solveSelectedCage()) {
                cheatedOnGame()
            }
        } else if (itemId == R.id.menu_show_solution) {
            game.solveGrid()
            cheatedOnGame()
        } else if (itemId == R.id.menu_swap_keypad) {
            keypadFrameHorizontalBias += 0.25f
            if (keypadFrameHorizontalBias == 1.0f) {
                keypadFrameHorizontalBias = 0.25f
            }
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.mainConstraintLayout)
            constraintSet.setHorizontalBias(R.id.keypadFrame, keypadFrameHorizontalBias)
            TransitionManager.beginDelayedTransition(binding.mainConstraintLayout)
            constraintSet.applyTo(binding.mainConstraintLayout)
        }
        return true
    }

    private fun gameSolved() {
        mTimerHandler.removeCallbacks(playTimer)
        grid.playTime = System.currentTimeMillis() - starttime
        showProgress(getString(R.string.puzzle_solved))
        binding.hint.isEnabled = false
        undoButton!!.isEnabled = false
        val statisticsManager = createStatisticsManager()
        val recordTime = statisticsManager.storeStatisticsAfterFinishedGame()
        val recordText = getString(R.string.puzzle_record_time)
        recordTime.ifPresent { record: String? -> showProgress("$recordText $record") }
        statisticsManager.storeStreak(true)
        val solvetime = grid.playTime
        val solveStr = convertTimetoStr(solvetime)
        topFragment!!.setGameTime(solveStr)
        val konfettiView = findViewById<KonfettiView>(R.id.konfettiView)
        val emitterConfig = Emitter(15L, TimeUnit.SECONDS).perSecond(150)

        val colors = listOf(
            MaterialColors.getColor(konfettiView, R.attr.colorPrimary),
            MaterialColors.getColor(konfettiView, R.attr.colorOnPrimary),
            MaterialColors.getColor(konfettiView, R.attr.colorSecondary),
            MaterialColors.getColor(konfettiView, R.attr.colorOnSecondary),
            MaterialColors.getColor(konfettiView, R.attr.colorTertiary),
            MaterialColors.getColor(konfettiView, R.attr.colorOnTertiary))

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
            binding.gridview.grid = currentGrid
            updateGameObject(currentGrid)
            val viewGroup = findViewById<ViewGroup>(R.id.container)
            TransitionManager.beginDelayedTransition(viewGroup, Fade(Fade.OUT))
            startFreshGrid(true)
            binding.gridview.visibility = View.VISIBLE
            binding.gridview.reCreate()
            binding.gridview.invalidate()
            binding.ferrisWheelView.visibility = View.INVISIBLE
            binding.ferrisWheelView.stopAnimation()
            TransitionManager.endTransitions(viewGroup)
        }
    }

    private fun cheatedOnGame() {
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
        val extras = data.extras
        val gridSizeString = extras!!.getString(Intent.EXTRA_TEXT)
        if (gridSizeString != null) {
            postNewGame(create(gridSizeString))
            return
        }
        if (requestCode != 7 || resultCode != RESULT_OK) {
            return
        }
        val filename = extras.getString("filename")
        Log.d("HoloKen", "Loading game: $filename")
        val saver = createWithFile(File(filename))
        restoreSaveGame(saver)
    }

    public override fun onPause() {
        if (grid.gridSize.amountOfNumbers > 0) {
            grid.playTime = System.currentTimeMillis() - starttime
            mTimerHandler.removeCallbacks(playTimer)
            // NB: saving solved games messes up the timer?
            val saver = createWithDirectory(this.filesDir)
            saver.Save(grid)
        }
        super.onPause()
    }

    public override fun onResume() {
        loadApplicationPreferences()
        if (grid.isActive) {
            binding.gridview.requestFocus()
            binding.gridview.invalidate()
            starttime = System.currentTimeMillis() - grid.playTime
            mTimerHandler.postDelayed(playTimer, 0)
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
        val theme: Theme = ApplicationPreferences.instance.theme
        if (theme === Theme.LIGHT) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else if (theme === Theme.DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        binding.gridview.updateTheme()
        if (ApplicationPreferences.instance.prefereneces!!.getBoolean("keepscreenon", true)
        ) {
            this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            this.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        if (!ApplicationPreferences.instance.prefereneces!!.getBoolean("showfullscreen", false)
        ) {
            this.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            this.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        topFragment!!.setTimerVisible(
            ApplicationPreferences.instance.prefereneces!!.getBoolean("showtimer", true)
        )
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
        if (insets == null) {
            return
        }
        runOnUiThread {
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.mainConstraintLayout)
            constraintSet.setGuidelineBegin(binding.mainTopAreaStart!!.id, rightEdgeOfCutOutArea)
            constraintSet.setGuidelineEnd(
                binding.mainTopAreaEnd!!.id,
                insets!!.getInsets(WindowInsetsCompat.Type.statusBars()).right
            )
            val topAreaBottom = max(
                (0.25 * this@MainActivity.resources.displayMetrics.xdpi).toInt(),
                insets!!.getInsets(WindowInsetsCompat.Type.statusBars()).bottom
            )
            constraintSet.setGuidelineBegin(binding.mainTopAreaBottom!!.id, topAreaBottom)
            constraintSet.applyTo(binding.mainConstraintLayout)
            binding.mainConstraintLayout.requestLayout()
        }
    }

    private val rightEdgeOfCutOutArea: Int
        get() {
            val cutout = insets!!.displayCutout
            return if (cutout == null || cutout.boundingRects.isEmpty()) {
                0
            } else cutout.boundingRects[0].right
        }

    fun createNewGame() {
        MainDialogs(this, game).newGameGridDialog()
    }

    private fun postNewGame(gridSize: GridSize) {
        if (grid.isActive) {
            createStatisticsManager().storeStreak(false)
        }
        val calculationService = GridCalculationService.instance
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
                if (gridSize.amountOfNumbers < 2) {
                    return@Thread
                }
                val calcService = GridCalculationService.instance
                calcService.calculateCurrentAndNextGrids(variant)
            }
            t.name = "PreviewCalculatorFromMainNonNext-" + variant.width + "x" + variant.height
            t.start()
        }
    }

    private fun updateGameObject(newGrid: Grid) {
        game = game.copy(grid = newGrid)
        keyPadFragment!!.setGame(game)
        topFragment!!.setGame(game)
    }

    @Synchronized
    fun startFreshGrid(newGame: Boolean) {
        game.clearUndoList()
        binding.gridview.updateTheme()
        binding.hint.isEnabled = true
        undoButton!!.isEnabled = false
        if (newGame) {
            createStatisticsManager().storeStatisticsAfterNewGame()
            starttime = System.currentTimeMillis()
            mTimerHandler.postDelayed(playTimer, 0)
            if (ApplicationPreferences.instance.prefereneces!!.getBoolean("pencilatstart", true)
            ) {
                grid.addPossiblesAtNewGame()
            }
        }
    }

    private fun restoreSaveGame(saver: SaveGame) {
        val grid = saver.restore()
        if (grid != null) {
            binding.gridview.grid = grid
            binding.gridview.rebuildCellsFromGrid()
            startFreshGrid(false)
            if (!this.grid.isSolved) {
                this.grid.isActive = true
            } else {
                this.grid.isActive = false
                if (this.grid.selectedCell != null) {
                    this.grid.selectedCell!!.isSelected = false
                }
                undoButton!!.isEnabled = false
                mTimerHandler.removeCallbacks(playTimer)
            }
            updateGameObject(grid)
            binding.gridview.invalidate()
            GridCalculationService.instance.setVariant(
                GameVariant(
                    binding.gridview.grid.gridSize,
                    instance().copy()
                )
            )
            //GridCalculationService.getInstance().calculateNextGrid();
        } else {
            MainDialogs(this, game).newGameGridDialog()
        }
    }

    private fun checkProgress() {
        val mistakes = grid.numberOfMistakes
        val filled = grid.numberOfFilledCells
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
        Snackbar.make(binding.hint, text, duration)
            .setAnchorView(binding.hint)
            .setAction("Undo") {
                game.restoreUndo()
                binding.gridview.invalidate()
                checkProgress()
            }
            .show()
    }

    private fun showProgress(string: String) {
        Snackbar.make(binding.hint, string, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.hint)
            .show()
    }

    private fun makeToast(resId: Int) {
        Snackbar.make(binding.hint, resId, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.hint)
            .show()
    }

    companion object {
        private const val UPDATE_RATE = 500
        private var insets: WindowInsetsCompat? = null
    }
}