package org.piepmeyer.gauguin.ui.newgame

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.google.android.material.sidesheet.SideSheetBehavior
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.calculation.GridPreviewState
import org.piepmeyer.gauguin.databinding.ActivityNewgameBinding
import org.piepmeyer.gauguin.ui.ActivityUtils
import org.piepmeyer.gauguin.ui.challenge.ChooseChallengeActivity

class NewGameActivity : AppCompatActivity() {
    private val activityUtils: ActivityUtils by inject()

    private lateinit var binding: ActivityNewgameBinding
    private val viewModel: NewGameViewModel by viewModel()
    private lateinit var shapeOptionsFragment: GridShapeOptionsFragment

    public override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        activityUtils.configureTheme(this)
        binding = ActivityNewgameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activityUtils.configureMainContainerBackground(binding.root)
        activityUtils.configureRootView(binding.root)

        activityUtils.configureFullscreen(this)

        binding.startnewgame.setOnClickListener { viewModel.viewModelScope.launch { startNewGame() } }

        binding.newGridPreview.isPreviewMode = true
        binding.newGridPreview.updateTheme()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.previewGridState.collect {
                    previewGridCalculated(it)
                }
            }
        }

        shapeOptionsFragment = GridShapeOptionsFragment()

        supportFragmentManager.commit {
            replace(R.id.newGameOptions, GridCellOptionsFragment())
            replace(R.id.newGameGridShapeOptions, shapeOptionsFragment)
        }

        binding.sideSheet?.let {
            val sideSheetBehavior = SideSheetBehavior.from(it)
            sideSheetBehavior.state = SideSheetBehavior.STATE_EXPANDED
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.newGameGridShapeOptions,
        ) { v, insets ->
            val innerPadding =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout(),
                )

            if (hasVerticalBaseLayout()) {
                v.setPadding(
                    innerPadding.left,
                    innerPadding.top,
                    innerPadding.right,
                    0,
                )
            } else {
                v.setPadding(
                    innerPadding.left,
                    innerPadding.top,
                    0,
                    innerPadding.bottom,
                )
            }

            WindowInsetsCompat.CONSUMED
        }

        binding.sideSheet?.let { sideSheet ->
            ViewCompat.setOnApplyWindowInsetsListener(
                sideSheet,
            ) { v, insets ->
                val innerPadding =
                    insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                            or WindowInsetsCompat.Type.displayCutout(),
                    )

                v.setPadding(
                    innerPadding.left,
                    0,
                    innerPadding.right,
                    innerPadding.bottom,
                )

                WindowInsetsCompat.CONSUMED
            }
        }
    }

    private fun previewGridCalculated(gridPreview: GridPreviewState) {
        val gridToPreview =
            when (gridPreview) {
                is GridPreviewState.GridPreviewNoGridAvailableYet -> null
                is GridPreviewState.GridPreviewStillCalculatingWithoutPreview -> null
                is GridPreviewState.GridPreviewStillCalculatingWithPreview -> gridPreview.previewGrid
                is GridPreviewState.GridPreviewCalculated -> gridPreview.grid
            }

        binding.newGridPreview.let {
            it.visibility =
                if (gridToPreview != null) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            if (gridToPreview != null) {
                it.grid = gridToPreview
                it.setPreviewStillCalculating(gridPreview.isStillCalculating)
                it.invalidate()
            }
        }
    }

    private fun hasVerticalBaseLayout(): Boolean = binding.sideSheet == null

    private suspend fun startNewGame() {
        val gridAlreadyCalculated =
            viewModel.viewModelScope
                .async {
                    viewModel.startNewGame()
                }.await()

        if (gridAlreadyCalculated) {
            binding.newGridPreview.isPreviewMode = false
            binding.newGridPreview.invalidate()

            finishAfterTransition()
        } else {
            finish()
        }
    }

    fun showChallenges() {
        val intent = Intent(this, ChooseChallengeActivity::class.java)

        this.startActivity(intent)

        finishAfterTransition()
    }
}
