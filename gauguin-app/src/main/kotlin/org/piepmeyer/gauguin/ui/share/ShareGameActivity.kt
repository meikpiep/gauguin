package org.piepmeyer.gauguin.ui.share

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.databinding.ActivitySharegameBinding
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.save.SavedGrid
import org.piepmeyer.gauguin.ui.ActivityUtils
import qrcode.QRCode
import kotlin.random.Random
import kotlin.random.nextInt

class ShareGameActivity : AppCompatActivity() {
    private val game: Game by inject()
    private val activityUtils: ActivityUtils by inject()

    private lateinit var binding: ActivitySharegameBinding

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        activityUtils.configureTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivitySharegameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activityUtils.configureMainContainerBackground(binding.root)
        activityUtils.configureRootView(binding.root)

        activityUtils.configureFullscreen(this)

        val serializedGrid = Json.encodeToString(SavedGrid.fromGrid(game.grid))

        println("serialized: ${serializedGrid.length}")
        println("serialized content: $serializedGrid")

        /*val bos = ByteArrayOutputStream()
        GZIPOutputStream(bos, true).bufferedWriter().use {
            it.write(serializedGrid)
            it.flush()
        }

        val compressedByteArray = bos.toByteArray()

        val compressedGrid = String(compressedByteArray)

        println("compressed size: ${compressedGrid.length}")
        println("compressed content: $compressedGrid")

        Files.write(Path.of("/data/user/0/org.piepmeyer.gauguin.debug/files/compressed-grid.zip"), compressedByteArray)*/

        val random = Random(31)

        /*
         * version: 8
         * width: 4
         * height: 4
         * showOperators: 1
         * cageOperation: 3
         * digitSetting: 3
         * difficulties: 3
         * singleCage: 2
         * numeral: 3
         * classicalRating (ignored)
         * human: 16
         * solvedViaHuman: 1
         * --> 48
         * per cell:
         *     cellNumber: 8
         *     row: 4
         *     column: 4
         *     value: (indexed) 5
         *     userValue: (indexed) 5
         *     possibles: 11
         *     --> 37
         *     --> 121 cells: 4477
         *     --> only store values: 121 * 5 = 605
         * stop-byte: 8
         * per cage:
         *     id: (ignored) 7
         *     action: 3
         *     type: 5
         *     result: (ignored) ?
         *     cellNumbers: (ignored) 4*7=28
         *     --> 8 * 60 = 480
         * undoSteps (ignored)
         * --> < 5.100 bit -> < 318 chars
         * --> without cell state: 48 + 605 + 480 = 1133 -> < 71 chars
         */
        val contentCharArray = CharArray(80)
        for (i in 0..<contentCharArray.size) {
            contentCharArray[i] = Char(random.nextInt(0..Char.MAX_VALUE.code))
        }
        val contentString = String(contentCharArray)

        print("contentByteArray: ")
        contentCharArray.forEach { print("${it.code} ") }
        println()
        println("contentString: $contentString")
        contentString.forEach { print("${it.code} ") }
        println()

        try {
            val squareQRCode =
                QRCode
                    .ofSquares()
                    .withInformationDensity(0)
                    .withColor(Color.WHITE)
                    .withBackgroundColor(Color.BLACK)
                    .build(contentString)

            val squarePngData = squareQRCode.renderToBytes()

            val bitmap = BitmapFactory.decodeByteArray(squarePngData, 0, squarePngData.size)

            binding.qrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            println(e.toString())
        }
    }
}
