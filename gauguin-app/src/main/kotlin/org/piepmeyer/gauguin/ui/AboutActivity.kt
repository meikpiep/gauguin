package org.piepmeyer.gauguin.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.databinding.ActivityAboutBinding
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.grid.GridCageAction

class AboutActivity : AppCompatActivity() {
    private val activityUtils: ActivityUtils by inject()
    private val game: Game by inject()

    private lateinit var binding: ActivityAboutBinding
    private var clicksOnPicture = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityUtils.configureFullscreen(this)

        binding.aboutPicture.setOnClickListener {
            clicksOnPicture++

            when (clicksOnPicture) {
                2 -> Snackbar.make(binding.root, "There is no easter egg.", Snackbar.LENGTH_LONG).show()
                3 -> Snackbar.make(binding.root, "There is really no easter egg.", Snackbar.LENGTH_LONG).show()
                4 -> Snackbar.make(binding.root, "Only procceed if you don't mind loosing your current game.", Snackbar.LENGTH_LONG).show()
                5 -> {
                    val grid =
                        GridBuilder(5, 5)
                            .addCage(10, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_HORIZONTAL, 2)
                            .addCage(10, GridCageAction.ACTION_MULTIPLY, GridCageType.TRIPLE_VERTICAL, 6)
                            .addCage(10, GridCageAction.ACTION_MULTIPLY, GridCageType.DOUBLE_HORIZONTAL, 2 + 20)
                            .addSingleCage(1, 0)
                            .addSingleCage(1, 1)
                            .addSingleCage(1, 0 + 5)
                            .addSingleCage(1, 2 + 5)
                            .addSingleCage(1, 3 + 5)
                            .addSingleCage(1, 4 + 5)
                            .addSingleCage(1, 0 + 10)
                            .addSingleCage(1, 2 + 10)
                            .addSingleCage(1, 2 + 15)
                            .addSingleCage(1, 3 + 15)
                            .addCage(10, GridCageAction.ACTION_MULTIPLY, GridCageType.ANGLE_LEFT_BOTTOM, 3 + 10)
                            .addSingleCage(1, 0 + 15)
                            .addSingleCage(1, 0 + 20)
                            .addSingleCage(1, 1 + 20)
                            .addSingleCage(1, 4 + 20)
                            .createGrid()

                    game.updateGrid(grid)

                    finishAfterTransition()
                }
            }
        }

        binding.aboutClose.setOnClickListener {
            finishAfterTransition()
        }
    }
}
