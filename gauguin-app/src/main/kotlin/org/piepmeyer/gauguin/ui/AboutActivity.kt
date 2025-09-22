package org.piepmeyer.gauguin.ui

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.ActivityAboutBinding

class AboutActivity : ComponentActivity() {
    private val activityUtils: ActivityUtils by inject()

    private lateinit var binding: ActivityAboutBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        activityUtils.configureTheme(this)
        super.onCreate(savedInstanceState)
//        binding = ActivityAboutBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        activityUtils.configureMainContainerBackground(binding.root)
//        activityUtils.configureRootView(binding.root)

        activityUtils.configureFullscreen(this)

        setContent {
            MessageCard("my name")
        }

        /*ViewCompat.setOnApplyWindowInsetsListener(
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
        }*/

        /*binding.aboutClose.setOnClickListener {
            finishAfterTransition()
        }*/

        /* binding.aboutShareApplicationLog.setOnClickListener {
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
        } */
    }

    @Composable
    fun MessageCard(name: String) {
        Image(
            painter = painterResource(R.drawable.navigation_drawer_header_gauguin_the_siesta),
            contentDescription = "Contact profile picture",
            modifier = Modifier.size(100.dp),
        )
        Text(text = "Hello $name!")
    }

    @Preview(name = "Light Mode")
    @Preview(
        uiMode = Configuration.UI_MODE_NIGHT_YES,
        showBackground = true,
        name = "Dark Mode",
    )
    @Composable
    fun previewMessageCard() {
        MessageCard("Android")
    }

}
