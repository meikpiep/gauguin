package org.piepmeyer.gauguin.ui.main

import android.content.Context
import android.content.res.Resources
import android.view.ContextThemeWrapper
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
    game: Game,
    private val context: Context,
    private val theme: Resources.Theme,
    private val lifecycleOwner: LifecycleOwner,
) {
    private val mistakes = game.grid.numberOfMistakes()
    private val text =
        resources.getQuantityString(
            R.plurals.game_info_popup_mistakes,
            mistakes,
            mistakes,
        )
    private val duration: Long =
        if (mistakes == 0) {
            1500
        } else {
            4000
        }

    private val usesCenterFab = binding.mainBottomAppBar.fabAlignmentMode == BottomAppBar.FAB_ALIGNMENT_MODE_CENTER

    private val balloonHeight = 64

    private val balloonWidth =
        min(
            (binding.mainBottomAppBar.measuredWidth / resources.displayMetrics.density * 0.8).toInt(),
            400,
        )

    private val foregroundColor =
        if (mistakes == 0) {
            MaterialColors.getColor(binding.root, R.attr.colorMainHintPopupSuccessForeground)
        } else {
            MaterialColors.getColor(binding.root, R.attr.colorMainHintPopupErrorsForeground)
        }

    private val backgroundColor =
        if (mistakes == 0) {
            MaterialColors.getColor(binding.root, R.attr.colorMainHintPopupSuccessBackground)
        } else {
            MaterialColors.getColor(binding.root, R.attr.colorMainHintPopupErrorsBackground)
        }

    private val balloonMarginBottom =
        if (usesCenterFab) {
            24
        } else {
            (binding.mainBottomAppBar.height - balloonHeight) / 2
        }

    fun show() {
        val balloon =
            createBalloon(ContextThemeWrapper(context, R.style.BalloonHintPopupTheme)) {
                iconDrawable =
                    if (mistakes == 0) {
                        ResourcesCompat.getDrawable(resources, R.drawable.checkbox_marked_circle_outline, theme)
                    } else {
                        ResourcesCompat.getDrawable(resources, R.drawable.alert_outline, theme)
                    }
                text = this@BalloonHintPopup.text
                textSize = 14f
                setBackgroundColor(this@BalloonHintPopup.backgroundColor)
                setTextColor(foregroundColor)
                setIconColor(foregroundColor)
                setWidth(balloonWidth)
                setHeight(balloonHeight)
                setIsVisibleArrow(false)
                paddingLeft = 16
                paddingRight = 16 + iconWidth + iconSpace
                // marginLeft = startMarginOfBottomAppBar / 2
                setCornerRadius(8f)
                setBalloonAnimation(BalloonAnimation.NONE)

                autoDismissDuration = duration
                setDismissWhenClicked(true)
                setDismissWhenLifecycleOnPause(true)
                dismissWhenTouchOutside = true
                setFocusable(false)

                setLifecycleOwner(this@BalloonHintPopup.lifecycleOwner)
                build()
            }

        balloon.showAlignBottom(
            binding.mainBottomAppBar,
            0,
            (-(balloonHeight + balloonMarginBottom) * resources.displayMetrics.density).toInt(),
        )
    }
}
