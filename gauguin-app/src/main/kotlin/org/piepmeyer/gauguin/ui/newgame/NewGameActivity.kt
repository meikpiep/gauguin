package org.piepmeyer.gauguin.ui.newgame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.sidesheet.SideSheetBehavior
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.calculation.GridPreviewCalculationService
import org.piepmeyer.gauguin.calculation.GridPreviewListener
import org.piepmeyer.gauguin.databinding.ActivityNewgameBinding
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.ui.ActivityUtils

class NewGameActivity : AppCompatActivity(), GridPreviewHolder, GridPreviewListener {
    private val applicationPreferences: ApplicationPreferences by inject()
    private val gameLifecycle: GameLifecycle by inject()
    private val activityUtils: ActivityUtils by inject()
    private val calculationService: GridCalculationService by inject()

    private val previewService = GridPreviewCalculationService()
    private lateinit var gridShapeOptionsFragment: GridShapeOptionsFragment
    private lateinit var cellOptionsFragment: GridCellOptionsFragment

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        val binding = ActivityNewgameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityUtils.configureFullscreen(this)

        val startNewGameButton = binding.startnewgame
        startNewGameButton.setOnClickListener { startNewGame() }

        val ft = supportFragmentManager.beginTransaction()
        cellOptionsFragment = GridCellOptionsFragment()
        cellOptionsFragment.setGridPreviewHolder(this)
        ft.replace(R.id.newGameOptions, cellOptionsFragment)
        ft.commit()

        binding.sideSheet?.let {
            val sideSheetBehavior = SideSheetBehavior.from(it)
            sideSheetBehavior.state = SideSheetBehavior.STATE_EXPANDED
        }

        binding.bottomSheet?.let {
            val bottomSheetBehavior = BottomSheetBehavior.from(it)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        val ft2 = supportFragmentManager.beginTransaction()
        gridShapeOptionsFragment = GridShapeOptionsFragment()
        gridShapeOptionsFragment.setGridPreviewHolder(this)
        ft2.replace(R.id.newGameGridShapeOptions, gridShapeOptionsFragment)
        ft2.commit()

        previewService.addListener(this)

        val variant = gameVariant()

        if (calculationService.hasCalculatedNextGrid(variant)) {
            previewService.takeCalculatedGrid(calculationService.consumeNextGrid())

            cellOptionsFragment.setGameVariant(variant)
        } else {
            refreshGrid()
        }
    }

    override fun onDestroy() {
        previewService.removeListener(this)
        super.onDestroy()
    }

    private fun startNewGame() {
        val variant = gameVariant()
        val grid = previewService.getGrid(variant)
        if (grid != null) {
            calculationService.variant = variant
            calculationService.nextGrid = grid
            gameLifecycle.startNewGame(grid)
        }

        gameLifecycle.postNewGame(startedFromMainActivityWithSameVariant = false)
        finishAfterTransition()
    }

    private fun gameVariant(): GameVariant =
        GameVariant(
            GridSize(
                applicationPreferences.gridWidth,
                applicationPreferences.gridHeigth,
            ),
            applicationPreferences.gameVariant,
        )

    override fun refreshGrid() {
        val variant = gameVariant()

        cellOptionsFragment.setGameVariant(variant)

        previewService.calculateGrid(variant, lifecycleScope)
    }

    override fun updateNumeralSystem() {
        gridShapeOptionsFragment.updateNumeralSystem()
    }

    override fun clearGrids() {
        previewService.clearGrids()
        previewService.calculateGrid(gameVariant(), lifecycleScope)
    }

    override fun previewGridCreated(
        grid: Grid,
        previewStillCalculating: Boolean,
    ) {
        runOnUiThread {
            grid.options.numeralSystem = applicationPreferences.gameVariant.numeralSystem
            gridShapeOptionsFragment.setGrid(grid)
            gridShapeOptionsFragment.updateGridUI(previewStillCalculating)
        }
    }

    override fun previewGridCalculated(grid: Grid) {
        runOnUiThread {
            gridShapeOptionsFragment.previewGridCalculated(grid)
        }
    }
}
