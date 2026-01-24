package org.piepmeyer.gauguin.ui.newgame

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.sidesheet.SideSheetBehavior
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.ActivityNewgameBinding
import org.piepmeyer.gauguin.ui.ActivityUtils

class NewGameActivity : AppCompatActivity() {
    private val activityUtils: ActivityUtils by inject()
    private lateinit var binding: ActivityNewgameBinding
    private lateinit var viewModel: NewGameViewModel
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

        val startNewGameButton = binding.startnewgame
        startNewGameButton.setOnClickListener { startNewGame() }

        viewModel = ViewModelProvider(this)[NewGameViewModel::class.java]

        shapeOptionsFragment = GridShapeOptionsFragment()

        supportFragmentManager.commit {
            replace(R.id.newGameOptions, GridCellOptionsFragment())
            replace(R.id.newGameGridShapeOptions, shapeOptionsFragment)
        }

        binding.sideSheet?.let {
            val sideSheetBehavior = SideSheetBehavior.from(it)
            sideSheetBehavior.state = SideSheetBehavior.STATE_EXPANDED
        }

        binding.bottomSheet?.let {
            val bottomSheetBehavior = BottomSheetBehavior.from(it)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
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

    private fun hasVerticalBaseLayout(): Boolean = binding.bottomSheet != null

    private fun startNewGame() {
        val gridAlreadyCalculated = viewModel.startNewGame()

        if (gridAlreadyCalculated) {
            shapeOptionsFragment.gridPreview().isPreviewMode = false
            shapeOptionsFragment.gridPreview().invalidate()

            finishAfterTransition()
        } else {
            finish()
        }
    }
}
