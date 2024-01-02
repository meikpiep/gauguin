package org.piepmeyer.gauguin.ui.newgame

import android.content.Intent
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
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.grid.GridSize
import org.piepmeyer.gauguin.options.GameVariant
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import org.piepmeyer.gauguin.ui.ActivityUtils

class NewGameActivity : AppCompatActivity(), GridPreviewHolder, GridPreviewListener {
    private val applicationPreferences: ApplicationPreferences by inject()
    private val activityUtils: ActivityUtils by inject()
    private val calculationService: GridCalculationService by inject()

    private val gridCalculator = GridPreviewCalculationService()
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

        gridCalculator.addListener(this)

        refreshGrid()
    }

    override fun onDestroy() {
        gridCalculator.removeListener(this)
        super.onDestroy()
    }

    private fun startNewGame() {
        val variant = gameVariant()
        val grid = gridCalculator.getGrid(variant)
        if (grid != null) {
            calculationService.variant = variant
            calculationService.nextGrid = grid
        }

        val intent = this.intent
        intent.action = Intent.ACTION_SEND

        this.setResult(99, intent)
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

        gridCalculator.calculateGrid(variant, lifecycleScope)
    }

    override fun updateNumeralSystem() {
        gridShapeOptionsFragment.updateNumeralSystem()
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
