package org.piepmeyer.gauguin.ui.main

import com.google.android.material.color.MaterialColors
import nl.dionsegijn.konfetti.core.PartyFactory
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

class KonfettiStarter(
    private val konfettiView: KonfettiView,
) {
    fun startKonfetti() {
        val emitterConfig = Emitter(8L, TimeUnit.SECONDS).perSecond(150)

        val colors =
            listOf(
                MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorPrimary),
                MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorOnPrimary),
                MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorSecondary),
                MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorOnSecondary),
                MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorTertiary),
                MaterialColors.getColor(konfettiView, com.google.android.material.R.attr.colorOnTertiary),
            )

        val party =
            PartyFactory(emitterConfig)
                .angle(270)
                .spread(90)
                .setSpeedBetween(1f, 5f)
                .timeToLive(3000L)
                .position(0.0, 0.0, 1.0, 0.0)
                .colors(colors)
                .build()
        konfettiView.start(party)
    }
}
