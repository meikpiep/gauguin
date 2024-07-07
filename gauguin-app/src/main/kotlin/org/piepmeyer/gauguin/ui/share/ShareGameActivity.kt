package org.piepmeyer.gauguin.ui.share

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.databinding.ActivitySharegameBinding
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.save.SavedGrid
import org.piepmeyer.gauguin.ui.ActivityUtils

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

        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(serializedGrid, BarcodeFormat.QR_CODE, 800, 800)
            binding.qrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            println(e.toString())
        }
    }
}
