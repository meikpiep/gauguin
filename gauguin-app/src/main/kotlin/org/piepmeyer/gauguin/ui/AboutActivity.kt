package org.piepmeyer.gauguin.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private val activityUtils: ActivityUtils by inject()

    private lateinit var binding: ActivityAboutBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityUtils.configureFullscreen(this)

        binding.aboutClose.setOnClickListener {
            finishAfterTransition()
        }
    }
}
