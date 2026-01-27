package org.piepmeyer.gauguin.ui.challenge

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.color.MaterialColors
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.databinding.ActivityChoosechallengeBinding
import org.piepmeyer.gauguin.difficulty.DisplayableGameDifficulty
import org.piepmeyer.gauguin.difficulty.GameDifficultyRatingService
import org.piepmeyer.gauguin.difficulty.GridDifficultyCalculator
import org.piepmeyer.gauguin.difficulty.ensureDifficultyCalculated
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.ui.ActivityUtils
import org.piepmeyer.gauguin.ui.difficulty.MainGameDifficultyLevelFragment

class ChooseChallengeActivity : AppCompatActivity() {
    private val activityUtils: ActivityUtils by inject()
    private val calculationService: GridCalculationService by inject()
    private val gameLifecycle: GameLifecycle by inject()
    private val difficultyService: GameDifficultyRatingService by inject()

    private lateinit var viewModel: ChallengesViewModel
    private lateinit var binding: ActivityChoosechallengeBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppThemeDay)
        super.onCreate(savedInstanceState)

        binding = ActivityChoosechallengeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ChallengesViewModel::class.java]

        val shapeAppearanceModel =
            ShapeAppearanceModel()
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, 15.0f)
                .build()

        val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
        shapeDrawable.fillColor =
            ColorStateList.valueOf(
                MaterialColors.compositeARGBWithAlpha(
                    MaterialColors.getColor(binding.classicalRatingLabel, com.google.android.material.R.attr.colorSecondaryFixed),
                    200,
                ),
            )

        binding.classicalRatingLabel.background = shapeDrawable
        binding.potentialSolutionCountLabel.background = shapeDrawable

        activityUtils.configureFullscreen(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.grids.collect {
                    updateGrids(it)
                }
            }
        }

        binding.startZen.setOnClickListener { startGrid(binding.challengeZen.grid) }
        binding.startChruncher.setOnClickListener { startGrid(binding.challengeChruncher.grid) }

        binding.sizeFour.setOnClickListener { viewModel.changeSize(4) }
        binding.sizeFive.setOnClickListener { viewModel.changeSize(5) }
        binding.sizeSix.setOnClickListener { viewModel.changeSize(6) }

        binding.sizeChipGroup.check(
            when (viewModel.gridSize) {
                4 -> R.id.size_four
                6 -> R.id.size_six
                else -> R.id.size_five
            },
        )

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.sizeChipGroup,
        ) { v, insets ->
            val innerPadding =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout(),
                )
            v.setPadding(
                0,
                innerPadding.top,
                0,
                0,
            )

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun updateGrids(grids: ChallengePair) {
        val zenGrid = grids.zenGrid
        val chruncherGrid = grids.chruncherGrid

        binding.challengeZen.grid = zenGrid
        binding.challengeChruncher.grid = chruncherGrid

        binding.challengeZen.invalidate()
        binding.challengeChruncher.invalidate()

        binding.classicalRatingZen.text = formattedDifficultyRating(zenGrid)
        binding.classicalRatingChruncher.text = formattedDifficultyRating(chruncherGrid)

        val primaryLocale = getResources().configuration.locales[0]

        binding.potentialSolutionCountZen.text =
            String.format(primaryLocale, "%,d", GridDifficultyCalculator(zenGrid).calculateRawDifficulty())
        binding.potentialSolutionCountChruncher.text =
            String.format(primaryLocale, "%,d", GridDifficultyCalculator(chruncherGrid).calculateRawDifficulty())
    }

    fun formattedDifficultyRating(grid: Grid): String {
        val rating = difficultyService.difficultyRating(grid.variant)

        grid.ensureDifficultyCalculated()
        val classicalDifficulty = grid.difficulty.classicalRating!!

        return MainGameDifficultyLevelFragment.formatDifficulty(
            DisplayableGameDifficulty(rating).displayableDifficultyValue(classicalDifficulty),
        )
    }

    private fun startGrid(grid: Grid) {
        gameLifecycle.startNewGame(grid)

        finishAfterTransition()
    }
}
