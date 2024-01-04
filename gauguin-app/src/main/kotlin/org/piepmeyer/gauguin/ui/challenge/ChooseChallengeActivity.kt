package org.piepmeyer.gauguin.ui.challenge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.calculation.CalculationMode
import org.piepmeyer.gauguin.calculation.GridCalculationService
import org.piepmeyer.gauguin.challenge.Challenges
import org.piepmeyer.gauguin.databinding.ActivityChoosechallengeBinding
import org.piepmeyer.gauguin.game.GameLifecycle
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.ui.ActivityUtils

class ChooseChallengeActivity : AppCompatActivity() {
    private val activityUtils: ActivityUtils by inject()
    private val calculationService: GridCalculationService by inject()
    private val gameLifecycle: GameLifecycle by inject()

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        val binding = ActivityChoosechallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityUtils.configureFullscreen(this)

        val challenges = Challenges()

        binding.challengeZen.grid = challenges.zenChallenge()
        binding.challengeChruncher.grid = challenges.chruncherChallenge()

        binding.challengeZen.setOnClickListener { startGrid(binding.challengeZen.grid) }
        binding.challengeChruncher.setOnClickListener { startGrid(binding.challengeChruncher.grid) }
    }

    private fun startGrid(grid: Grid) {
        calculationService.mode = CalculationMode.PlaySingleChallenge
        calculationService.variant = grid.variant
        calculationService.setNextGrid(grid)

        gameLifecycle.startNewGame(grid)
        gameLifecycle.postNewGame(startedFromMainActivityWithSameVariant = false)

        finishAfterTransition()
    }
}
