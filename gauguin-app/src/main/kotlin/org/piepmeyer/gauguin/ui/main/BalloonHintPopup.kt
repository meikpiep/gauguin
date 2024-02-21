package org.piepmeyer.gauguin.ui.main

import android.content.Context
import android.content.res.Resources
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.color.MaterialColors
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.ActivityMainBinding
import org.piepmeyer.gauguin.game.Game
import kotlin.math.min

class BalloonHintPopup(
    private val binding: ActivityMainBinding,
    private val resources: Resources,
    private val game: Game,
    private val context: Context,
    private val theme: Resources.Theme,
    private val lifecycleOwner: LifecycleOwner,
) {
    fun show() {
        if (game.grid.isSolved()) {
            return
        }

        val mistakes = game.grid.numberOfMistakes()
        val text =
            resources.getQuantityString(
                R.plurals.toast_mistakes,
                mistakes,
                mistakes,
            )
        val duration: Long =
            if (mistakes == 0) {
                1500
            } else {
                4000
            }

        val usesCenterFab = binding.mainBottomAppBar.fabAlignmentMode == BottomAppBar.FAB_ALIGNMENT_MODE_CENTER

        val balloonHeight = 64
        val balloonWidth =
            min(
                (binding.mainBottomAppBar.width * 0.9).toInt(),
                400,
            )

        val startMarginOfBottomAppBar =
            (binding.mainBottomAppBar.layoutParams as ViewGroup.MarginLayoutParams)
                .marginStart

        val foregroundColor =
            if (mistakes == 0) {
                MaterialColors.getColor(binding.root, R.attr.colorMainHintPopupSuccessForeground)
            } else {
                MaterialColors.getColor(binding.root, R.attr.colorMainHintPopupErrorsForeground)
            }

        val backgroundColor =
            if (mistakes == 0) {
                MaterialColors.getColor(binding.root, R.attr.colorMainHintPopupSuccessBackground)
            } else {
                MaterialColors.getColor(binding.root, R.attr.colorMainHintPopupErrorsBackground)
            }

        val balloon =
            createBalloon(context) {
                iconDrawable =
                    if (mistakes == 0) {
                        ResourcesCompat.getDrawable(resources, R.drawable.checkbox_marked_circle_outline, theme)
                    } else {
                        ResourcesCompat.getDrawable(resources, R.drawable.alert_outline, theme)
                    }
                setText(text)
                textSize = 14f
                setBackgroundColor(backgroundColor)
                setTextColor(foregroundColor)
                setIconColor(foregroundColor)
                setWidth(balloonWidth)
                setHeight(balloonHeight)
                setIsVisibleArrow(false)
                paddingLeft = 16
                paddingRight = 16 + iconWidth + iconSpace
                marginLeft = startMarginOfBottomAppBar
                setCornerRadius(8f)
                setBalloonAnimation(BalloonAnimation.NONE)
                autoDismissDuration = duration
                setDismissWhenClicked(true)
                setDismissWhenLifecycleOnPause(true)
                dismissWhenTouchOutside = true
                setLifecycleOwner(this@BalloonHintPopup.lifecycleOwner)
            }

        val balloonMarginBottom =
            if (usesCenterFab) {
                24
            } else {
                (binding.mainBottomAppBar.height - balloonHeight) / 2
            }

        balloon.showAlignBottom(
            binding.root,
            0,
            (-(balloonHeight + balloonMarginBottom) * resources.displayMetrics.density).toInt(),
        )
    }
}
