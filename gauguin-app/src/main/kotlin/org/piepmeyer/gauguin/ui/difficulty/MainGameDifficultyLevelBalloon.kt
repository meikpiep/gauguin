package org.piepmeyer.gauguin.ui.difficulty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.color.MaterialColors
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.createBalloon
import org.piepmeyer.gauguin.difficulty.GameDifficulty
import org.piepmeyer.gauguin.options.GameVariant

class MainGameDifficultyLevelBalloon(
    private val difficulty: GameDifficulty?,
    private val variant: GameVariant,
) {
    fun showBalloon(baseView: View, inflater: LayoutInflater, parent: ViewGroup, lifecycleOwner: LifecycleOwner, anchorView: View) {
        val difficultyFragment = MainGameDifficultyLevelFragment(difficulty, variant)

        val view = difficultyFragment.onCreateView(inflater, parent, null)

        val balloon = createBalloon(baseView.context) {
            setLayout(view)
            setWidth(BalloonSizeSpec.WRAP)
            setHeight(BalloonSizeSpec.WRAP)
            setBackgroundColor(MaterialColors.getColor(
                baseView,
                com.google.android.material.R.attr.colorSecondaryContainer
            )
            )
            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            setArrowSize(10)
            setArrowPosition(0.5f)
            setPadding(8)
            paddingBottom = 16
            setCornerRadius(8f)
            setBalloonAnimation(BalloonAnimation.ELASTIC)

            setLifecycleOwner(lifecycleOwner)

            build()
        }

        balloon.showAlignBottom(anchorView)
    }
}
