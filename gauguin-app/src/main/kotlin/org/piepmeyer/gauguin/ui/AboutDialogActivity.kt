package org.piepmeyer.gauguin.ui

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.ActivityDialogAboutBinding

class AboutDialogActivity : AppCompatActivity() {
    private val activityUtils: ActivityUtils by inject()

    private lateinit var binding: ActivityDialogAboutBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityDialogAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityUtils.configureFullscreen(this)

        val avd = binding.aboutIcon

        avd.drawable.apply {
            when (this) {
                is AnimatedVectorDrawableCompat -> this.start()
                is AnimatedVectorDrawable -> this.start()
            }
        }

        binding.aboutClose.setOnClickListener {
            finishAfterTransition()
        }
    }
}
