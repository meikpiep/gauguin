package com.holokenmod.ui.newgame

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
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
import org.koin.android.ext.android.inject
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class NewGameActivity : AppCompatActivity(), GridPreviewHolder {
    private val applicationPreferences: ApplicationPreferences by inject()
    private val calculationService: GridCalculationService by inject()

    private val gridCalculator = GridPreviewCalculationService()
    private var gridFuture: Future<Grid>? = null
    private var gridShapeOptionsFragment: GridShapeOptionsFragment? = null
    private var cellOptionsFragment: GridCellOptionsFragment? = null

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
        cellOptionsFragment!!.setGridPreviewHolder(this)
        ft.replace(R.id.newGameOptions, cellOptionsFragment!!)
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
        gridShapeOptionsFragment!!.setGridPreviewHolder(this)
        ft2.replace(R.id.newGameGridShapeOptions, gridShapeOptionsFragment!!)
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
        if (gridFuture != null && !gridFuture!!.isDone) {
            gridFuture!!.cancel(true)
        }
        val variant = GameVariant(
            gridSize,
            CurrentGameOptionsVariant.instance
        )

        cellOptionsFragment!!.setGameVariant(variant)

        gridFuture = gridCalculator.getOrCreateGrid(variant)
        var grid: Grid? = null
        var previewStillCalculating = false
        try {
            grid = gridFuture!![250, TimeUnit.MILLISECONDS]
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            val variantWithoutDifficulty = variant.copy(
                options = variant.options.copy(difficultySetting = DifficultySetting.ANY)
            )

            grid = GridCreator(variantWithoutDifficulty).createRandomizedGridWithCages()
            previewStillCalculating = true
        }

        gridShapeOptionsFragment!!.setGrid(grid!!)

        gridShapeOptionsFragment!!.updateGridUI(previewStillCalculating)

        if (previewStillCalculating) {
            val gridPreviewThread = Thread { createPreview() }
            gridPreviewThread.name = "PreviewFromNew-" + variant.width + "x" + variant.height
            gridPreviewThread.start()
        }
    }

    private fun createPreview() {
        try {
            val gridFuture = gridCalculator.getOrCreateGrid(
                GameVariant(
                    gridSize,
                    CurrentGameOptionsVariant.instance
                )
            )
            val previewGrid = gridFuture.get()
            val finalPreviewGrid = previewGrid as Grid
            previewGridCalculated(finalPreviewGrid)
        } catch (ex: ExecutionException) {
            ex.printStackTrace()
        } catch (ex: InterruptedException) {
            ex.printStackTrace()
        }
    }

    private fun previewGridCalculated(grid: Grid) {
        runOnUiThread {

            //TransitionManager.beginDelayedTransition(findViewById(R.id.newGame));
            gridShapeOptionsFragment!!.previewGridCalculated(grid)
        }
    }
}