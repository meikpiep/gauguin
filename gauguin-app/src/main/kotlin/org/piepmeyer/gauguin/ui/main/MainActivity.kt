package org.piepmeyer.gauguin.ui.main

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.ActivityMainBinding
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.ui.ActivityUtils
import org.piepmeyer.gauguin.ui.MainDialogs
import org.piepmeyer.gauguin.ui.newgame.NewGameActivity

private val logger = KotlinLogging.logger {}

class MainActivity : AppCompatActivity() {
    private val game: Game by inject()
    private val gameLifecycle: GameLifecycle by inject()
    private val applicationPreferences: ApplicationPreferences by inject()
    private val activityUtils: ActivityUtils by inject()

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomAppBarService: MainBottomAppBarService

    private var keepScreenOn: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        activityUtils.configureTheme(this)

        binding = ActivityMainBinding.inflate(layoutInflater)

        configureActivity()

        setContentView(binding.root)
        activityUtils.configureRootView(binding.root)

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)

        game.gridUI = binding.gridview
        binding.gridview.setOnLongClickListener {
            game.longClickOnSelectedCell()
        }
        binding.gridview.setOnKeyListener(GridUIOnKeyListener(this))

        binding.gridview.grid = game.grid

        val layoutTagMainActivity = binding.root.tag as String?

        val tinyModeOfTopFragment = layoutTagMainActivity?.contains("tiny-top-fragment") ?: false
        val gridViewNeedsTopPadding = layoutTagMainActivity?.contains("grid-view-top-padding") ?: false
        val gridViewNeedsBottomPadding = layoutTagMainActivity?.contains("grid-view-bottom-padding") ?: false
        val gridViewNeedsStartPadding = layoutTagMainActivity?.contains("grid-view-start-padding") ?: false
        val gridViewNeedsEndPadding = layoutTagMainActivity?.contains("grid-view-end-padding") ?: false

        val topFragment = GameTopFragment()
        topFragment.tinyMode = tinyModeOfTopFragment

        supportFragmentManager.commit {
            replace(R.id.keypadFrame, KeyPadFragment())
            replace(R.id.fastFinishingModeFrame, FastFinishingModeFragment())
            replace(R.id.gameSolvedFrame, GameSolvedFragment())
            replace(R.id.gameTopFrame, topFragment)
        }

        registerForContextMenu(binding.gridview)

        bottomAppBarService = MainBottomAppBarService(this, binding)
        bottomAppBarService.initialize()

        val navigationViewService = MainNavigationViewService(this, binding)
        navigationViewService.initialize()

        FerrisWheelConfigurer(binding.ferrisWheelView).configure()

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

        initializeInsets(
            gridViewNeedsTopPadding,
            gridViewNeedsBottomPadding,
            gridViewNeedsStartPadding,
            gridViewNeedsEndPadding,
        )

        val viewModel: MainViewModel by viewModels()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    reactOnUiState(it.state)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.nextGridState
                    .combine(viewModel.uiState) { nextGridState, mainUiState ->
                        Pair(nextGridState, mainUiState.state)
                    }.collect {
                        reactOnNextGridState(it)
                    }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.keepScreenOnState.collect {
                    keepScreenOn = it

                    activityUtils.configureKeepScreenOn(this@MainActivity, keepScreenOn)
                }
            }
        }

        resources.configuration.apply {
            logger.debug {
                "MainActivity configuration change," +
                    " size ${this.screenWidthDp}x${this.screenHeightDp}," +
                    " dpi ${this.densityDpi}," +
                    " orientation ${this.orientation}," +
                    " screen layout ${this.screenLayout}"
            }
        }

        MainDialogs(this).openNewUserHelpDialog()
    }

    private fun initializeInsets(
        gridViewNeedsTopPadding: Boolean,
        gridViewNeedsBottomPadding: Boolean,
        gridViewNeedsStartPadding: Boolean,
        gridViewNeedsEndPadding: Boolean,
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(
            binding.gameTopFrame,
        ) { v, insets ->
            val innerPadding =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout(),
                )
            v.setPadding(
                innerPadding.left,
                innerPadding.top,
                innerPadding.right,
                0,
            )

            WindowInsetsCompat.CONSUMED
        }

        if (gridViewNeedsTopPadding || gridViewNeedsBottomPadding || gridViewNeedsStartPadding || gridViewNeedsEndPadding) {
            ViewCompat.setOnApplyWindowInsetsListener(
                binding.gridview,
            ) { v, insets ->
                val innerPadding =
                    insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                            or WindowInsetsCompat.Type.displayCutout(),
                    )
                v.setPadding(
                    if (gridViewNeedsStartPadding) innerPadding.left else 0,
                    if (gridViewNeedsTopPadding) innerPadding.top else 0,
                    if (gridViewNeedsEndPadding) innerPadding.right else 0,
                    if (gridViewNeedsBottomPadding) innerPadding.bottom else 0,
                )

                WindowInsetsCompat.CONSUMED
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.mainBottomAppBar,
        ) { v, insets ->
            val innerPadding =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout(),
                )

            val right = binding.gridview.right
            val useMarginOfGridView = v.marginStart != 0 && right > 0

            val additionalLeftPadding =
                if (useMarginOfGridView) {
                    right
                } else {
                    0
                }

            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = innerPadding.left + additionalLeftPadding
                rightMargin = innerPadding.right
                bottomMargin = innerPadding.bottom
            }

            WindowInsetsCompat.CONSUMED
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.keypadFrame,
        ) { v, insets ->
            val innerPadding =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout(),
                )
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = innerPadding.left
                rightMargin = innerPadding.right
                bottomMargin = innerPadding.bottom
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun reactOnUiState(state: MainUiState) {
        runOnUiThread {
            bottomAppBarService.updateAppBarState(state)

            when (state) {
                MainUiState.CALCULATING_NEW_GRID ->
                    {
                        binding.gridview.visibility = View.INVISIBLE
                        binding.ferrisWheelView.visibility = View.VISIBLE
                        binding.ferrisWheelView.startAnimation()
                    }

                MainUiState.PLAYING ->
                    {
                        binding.gridview.visibility = View.VISIBLE

                        binding.ferrisWheelView.visibility = View.INVISIBLE
                        binding.ferrisWheelView.stopAnimation()

                        if (applicationPreferences.stopConfettiImmediatelyWhenStartingNewGame) {
                            binding.konfettiView.reset()
                        } else {
                            binding.konfettiView.stopGracefully()
                        }

                        updateMainGridCellShape()
                        updateNumeralSystemIcon()

                        binding.gridview.reCreate()
                        binding.gridview.invalidate()
                    }

                MainUiState.ALREADY_SOLVED -> {}
                MainUiState.SOLVED -> {
                    if (!game.grid.isCheated()) {
                        KonfettiStarter(binding.konfettiView).startKonfetti()
                    }
                }
            }
        }
    }

    private fun reactOnNextGridState(statePair: Pair<NextGridState, MainUiState>) {
        runOnUiThread {
            binding.pendingNextGridCalculation.visibility =
                when {
                    statePair.second in listOf(MainUiState.SOLVED, MainUiState.ALREADY_SOLVED) -> View.INVISIBLE
                    statePair.first == NextGridState.CURRENTLY_CALCULATING -> View.VISIBLE
                    else -> View.INVISIBLE
                }
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

    public override fun onResume() {
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
        activityUtils.configureMainContainerBackground(binding.container)
        activityUtils.configureKeepScreenOn(this, keepScreenOn)
        activityUtils.configureFullscreen(this)

        binding.gridview.updateTheme(activityUtils)
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

        val insets = checkNotNull(ViewCompat.getRootWindowInsets(binding.root))

        val systemInsets =
            insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                    or WindowInsetsCompat.Type.displayCutout(),
            )

        BalloonHintPopup(binding, resources, game, applicationContext, theme, systemInsets, this).show()

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
