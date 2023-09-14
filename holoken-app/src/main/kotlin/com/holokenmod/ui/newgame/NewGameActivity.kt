package com.holokenmod.ui.newgame

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.sidesheet.SideSheetBehavior
import com.holokenmod.R
import com.holokenmod.calculation.GridCalculationService
import com.holokenmod.calculation.GridPreviewCalculationService
import com.holokenmod.creation.GridCreator
import com.holokenmod.databinding.ActivityNewgameBinding
import com.holokenmod.grid.Grid
import com.holokenmod.grid.GridSize
import com.holokenmod.options.ApplicationPreferences
import com.holokenmod.options.CurrentGameOptionsVariant
import com.holokenmod.options.DifficultySetting
import com.holokenmod.options.GameVariant
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.android.ext.android.inject

private val logger = KotlinLogging.logger {}

class NewGameActivity : AppCompatActivity(), GridPreviewHolder {
    private val applicationPreferences: ApplicationPreferences by inject()
    private val calculationService: GridCalculationService by inject()

    private val gridCalculator = GridPreviewCalculationService()
    private lateinit var gridShapeOptionsFragment: GridShapeOptionsFragment
    private lateinit var cellOptionsFragment: GridCellOptionsFragment
    private var lastVariant: GameVariant? = null
    private var lastGridCalculation: Deferred<Grid>? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        val binding = ActivityNewgameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!applicationPreferences.preferences.getBoolean("showfullscreen", false)
        ) {
            this.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            this.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
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

        refreshGrid()
    }

    private fun startNewGame() {
        val variant = GameVariant(
            gridSize,
            applicationPreferences.gameVariant
        )
        val grid = gridCalculator.getGrid(variant)
        if (grid != null) {
            calculationService.setVariant(variant)
            calculationService.setNextGrid(grid)
        }

        val intent = this.intent
        intent.action = Intent.ACTION_SEND

        this.setResult(99, intent)
        finishAfterTransition()
    }

    private val gridSize: GridSize
        get() = GridSize(
            applicationPreferences.gridWidth,
            applicationPreferences.gridHeigth
        )

    @Synchronized
    override fun refreshGrid() {
        val variant = GameVariant(
            gridSize,
            CurrentGameOptionsVariant.instance
        )

        if (lastVariant == variant) {
            return
        }

        lastVariant = variant

        cellOptionsFragment.setGameVariant(variant)

        var grid: Grid?
        var previewStillCalculating: Boolean

        lifecycleScope.launch(Dispatchers.Default) {
            logger.info { "Generating real grid..." }

            lastGridCalculation?.cancel()
            val gridCalculation = async { gridCalculator.getOrCreateGrid(variant) }
            lastGridCalculation = gridCalculation

            val gridAfterShortTimeout = withTimeoutOrNull(250) { gridCalculation.await() }

            if (gridAfterShortTimeout == null) {
                logger.info { "Generating pseudo grid..." }
                val variantWithoutDifficulty = variant.copy(
                    options = variant.options.copy(difficultySetting = DifficultySetting.ANY)
                )

                grid = GridCreator(variantWithoutDifficulty).createRandomizedGridWithCages()
                previewStillCalculating = true
                logger.info { "Finished generating pseudo grid." }
            } else {
                logger.info { "Generated real grid with short timeout." }
                grid = gridAfterShortTimeout
                previewStillCalculating = false
            }

            runOnUiThread {
                gridShapeOptionsFragment.setGrid(grid!!)
                gridShapeOptionsFragment.updateGridUI(previewStillCalculating)
            }

            if (previewStillCalculating) {
                launch {
                    previewGridCalculated(gridCalculation.await())
                }
            }
        }
    }

    private fun previewGridCalculated(grid: Grid) {
        runOnUiThread {
            gridShapeOptionsFragment.previewGridCalculated(grid)
        }
    }
}