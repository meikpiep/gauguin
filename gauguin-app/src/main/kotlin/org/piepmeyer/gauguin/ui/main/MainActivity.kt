package org.piepmeyer.gauguin.ui.main

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.calculation.GridCalculationListener
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.databinding.ActivityMainBinding
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.game.GameSolvedListener
import org.piepmeyer.gauguin.game.GridCreationListener
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.ui.ActivityUtils
import org.piepmeyer.gauguin.ui.MainDialogs
import org.piepmeyer.gauguin.ui.newgame.NewGameActivity

private val logger = KotlinLogging.logger {}

class MainActivity :
    AppCompatActivity(),
    GridCreationListener,
    GameSolvedListener {
    private val game: Game by inject()
    private val gameLifecycle: GameLifecycle by inject()
    private val calculationService: GridCalculationService by inject()
    private val applicationPreferences: ApplicationPreferences by inject()
    private val activityUtils: ActivityUtils by inject()

    private lateinit var binding: ActivityMainBinding
    private lateinit var topFragment: GameTopFragment
    private lateinit var bottomAppBarService: MainBottomAppBarService

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

        binding.gridview.grid = game.grid

        val ft = supportFragmentManager.beginTransaction()
        topFragment = GameTopFragment()
        ft.replace(R.id.keypadFrame, KeyPadFragment())
        ft.replace(R.id.fastFinishingModeFrame, FastFinishingModeFragment())
        ft.replace(R.id.gameSolvedFrame, GameSolvedFragment())
        ft.replace(R.id.gameTopFrame, topFragment)
        ft.commit()

        game.addGameSolvedHandler(this)
        game.addGridCreationListener(this)

        registerForContextMenu(binding.gridview)

        bottomAppBarService = MainBottomAppBarService(this, binding)
        bottomAppBarService.initialize()

        val navigationViewService = MainNavigationViewService(this, binding)
        navigationViewService.initialize()

        calculationService.addListener(createGridCalculationListener())
        configureActivity()

        val specialListener =
            OnSharedPreferenceChangeListener { _: SharedPreferences, key: String? ->
                if (key == "theme" || key == "maximumCellSize") {
                    this.recreate()
                } else if (key == "gridTakesRemainingSpaceIfNecessary") {
                    updateMainGridCellShape()
                }
            }

        val preferences = PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        preferences.registerOnSharedPreferenceChangeListener(specialListener)

        freshGridWasCreated()

        bottomAppBarService.updateAppBarState()
        navigationViewService.updateMainBottomBarMargins()

        MainDialogs(this).openNewUserHelpDialog()
    }

    override fun onDestroy() {
        super.onDestroy()

        game.removeGameSolvedHandler(this)
        game.removeGridCreationListener(this)
    }

    private fun createGridCalculationListener(): GridCalculationListener =
        object : GridCalculationListener {
            override fun startingCurrentGridCalculation() {
                runOnUiThread {
                    binding.gridview.visibility = View.INVISIBLE
                    binding.ferrisWheelView.visibility = View.VISIBLE
                    binding.ferrisWheelView.startAnimation()
                }
            }

            override fun currentGridCalculated() {
                runOnUiThread {
                    binding.gridview.visibility = View.VISIBLE

                    binding.ferrisWheelView.visibility = View.INVISIBLE
                    binding.ferrisWheelView.stopAnimation()
                }
            }

            override fun startingNextGridCalculation() {
                runOnUiThread {
                    binding.pendingNextGridCalculation.visibility = View.VISIBLE
                }
            }

            override fun nextGridCalculated() {
                runOnUiThread {
                    binding.pendingNextGridCalculation.visibility = View.INVISIBLE
                }
            }
        }

    override fun puzzleSolved(troughReveal: Boolean) {
        bottomAppBarService.updateAppBarState()

        if (!troughReveal) {
            KonfettiStarter(binding.konfettiView).startKonfetti()
        }
    }

    private fun updateMainGridCellShape() {
        val newCellShape =
            MainActivityGridCellShapeService(
                binding.gridview,
                applicationPreferences,
            ).calculateCellShape()

        if (binding.gridview.cellShape != newCellShape) {
            binding.gridview.cellShape = newCellShape
            binding.gridview.requestLayout()
        }
    }

    public override fun onPause() {
        gameLifecycle.pauseGame()

        super.onPause()
    }

    /*override fun onStop() {
        gameLifecycle.pauseGame()

        super.onStop()
    }*/

    public override fun onResume() {
        gameLifecycle.setCoroutineScope(this.lifecycleScope)

        configureActivity()

        binding.konfettiView.reset()

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

    private fun configureActivity() {
        activityUtils.configureNightMode()
        activityUtils.configureKeepScreenOn(this)
        activityUtils.configureFullscreen(this)

        binding.gridview.updateTheme()
    }

    fun showNewGameDialog() {
        val intent = Intent(this, NewGameActivity::class.java)

        val options =
            ActivityOptions.makeSceneTransitionAnimation(
                this,
                game.gridUI as View,
                "grid",
            )

        startActivity(intent, options.toBundle())
    }

    private fun startNewGame() {
        runOnUiThread {
            binding.konfettiView.stopGracefully()

            updateMainGridCellShape()
            updateNumeralSystemIcon()
            bottomAppBarService.updateAppBarState()

            binding.gridview.reCreate()
            binding.gridview.invalidate()
        }
    }

    override fun freshGridWasCreated() {
        runOnUiThread {
            binding.ferrisWheelView.visibility = View.INVISIBLE
            binding.ferrisWheelView.stopAnimation()
            binding.pendingNextGridCalculation.visibility = View.INVISIBLE
        }

        startNewGame()

        calculationService.variant =
            GameVariant(
                binding.gridview.grid.gridSize,
                applicationPreferences.gameVariant.copy(),
            )
    }

    private fun updateNumeralSystemIcon() {
        if (game.grid.variant.options.numeralSystem == NumeralSystem.Decimal) {
            binding.numeralSystem.visibility = View.INVISIBLE
        } else {
            binding.numeralSystem.visibility = View.VISIBLE
            binding.numeralSystem.setImageResource(
                when (game.grid.variant.options.numeralSystem) {
                    NumeralSystem.Binary -> R.drawable.numeric_2_box_outline
                    NumeralSystem.Quaternary -> R.drawable.numeric_4_box_outline
                    NumeralSystem.Octal -> R.drawable.numeric_8_box_outline
                    NumeralSystem.Hexadecimal -> R.drawable.alpha_f_box_outline
                    NumeralSystem.Decimal -> R.drawable.baseline_pending_20
                },
            )
        }
    }

    fun checkProgress() {
        if (game.grid.isSolved()) {
            return
        }

        logger.info { "Going to show the hint popup from grid" }
        logger.info { game.grid.detailedToString() }

        BalloonHintPopup(binding, resources, game, applicationContext, theme, this).show()

        logger.info { "Showing the hint popup from grid" }
        logger.info { game.grid.detailedToString() }
    }

    fun gameSaved() {
        Snackbar
            .make(
                binding.root,
                resources.getText(R.string.main_activity_application_bar_item_current_game_saved),
                Snackbar.LENGTH_LONG,
            ).show()
    }
}
