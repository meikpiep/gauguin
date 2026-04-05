package org.piepmeyer.gauguin.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.creation.GridBuilder
import org.piepmeyer.gauguin.creation.cage.GridCageType
import org.piepmeyer.gauguin.databinding.ActivityAboutBinding
import org.piepmeyer.gauguin.game.Game

class AboutActivity : AppCompatActivity() {
    private val activityUtils: ActivityUtils by inject()
    private val game: Game by inject()

    private lateinit var binding: ActivityAboutBinding
    private var clicksOnPicture = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        activityUtils.configureTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activityUtils.configureMainContainerBackground(binding.root)
        activityUtils.configureRootView(binding.root)

        activityUtils.configureFullscreen(this)

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root,
        ) { v, insets ->
            val innerPadding =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout(),
                )
            v.setPadding(
                0,
                0,
                0,
                innerPadding.bottom,
            )

            WindowInsetsCompat.CONSUMED
        }

        binding.aboutPicture.setOnClickListener {
            clicksOnPicture++

            when (clicksOnPicture) {
                2 -> {
                    Snackbar.make(binding.root, "There is no easter egg.", Snackbar.LENGTH_LONG).show()
                }

                3 -> {
                    Snackbar.make(binding.root, "There is really no easter egg.", Snackbar.LENGTH_LONG).show()
                }

                4 -> {
                    Snackbar.make(binding.root, "Only procceed if you don't mind loosing your current game.", Snackbar.LENGTH_LONG).show()
                }

                5 -> {
                    val grid =
                        GridBuilder(5, 5)
                            .addCageSingle(1)
                            .addCageSingle(1)
                            .addCageMultiply(10, GridCageType.TRIPLE_HORIZONTAL)
                            .addCageSingle(1)
                            .addCageSingle(1)
                            .addCageSingle(1)
                            .addCageMultiply(10, GridCageType.TRIPLE_VERTICAL)
                            .addCageSingle(1)
                            .addCageSingle(1)
                            .addCageSingle(1)
                            .addCageSingle(1)
                            .addCageSingle(1)
                            .addCageMultiply(10, GridCageType.ANGLE_LEFT_BOTTOM)
                            .addCageSingle(1)
                            .addCageSingle(1)
                            .addCageSingle(1)
                            .addCageSingle(1)
                            .addCageMultiply(10, GridCageType.DOUBLE_HORIZONTAL)
                            .createGrid()

                    grid.addPossiblesAtNewGame()

                    game.useGrid(grid)

                    finishAfterTransition()
                }
            }
        }

        binding.aboutClose.setOnClickListener {
            finishAfterTransition()
        }

        binding.aboutShareApplicationLog.setOnClickListener {
            val reversedLines =
                Runtime
                    .getRuntime()
                    .exec("logcat -d")
                    .inputStream
                    .bufferedReader()
                    .readLines()
                    .reversed()
                    .toMutableList()

            var logLength = 0
            val logLines = mutableListOf<String>()

            while (reversedLines.isNotEmpty() && logLength < 100_000) {
                logLines += reversedLines.first()
                logLength += reversedLines.first().length

                reversedLines.removeAt(0)
            }

            val logText = logLines.reversed().joinToString(separator = System.lineSeparator())

            val sendIntent: Intent =
                Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, logText)
                    type = "text/plain"
                }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }
}
