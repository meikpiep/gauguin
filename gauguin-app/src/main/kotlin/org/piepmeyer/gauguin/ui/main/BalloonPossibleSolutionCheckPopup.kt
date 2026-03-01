package org.piepmeyer.gauguin.ui.main

import android.content.Context
import android.content.res.Resources
import android.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.color.MaterialColors
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.ActivityMainBinding
import org.piepmeyer.gauguin.game.PossibleSolutionCheckState
import kotlin.math.min

class BalloonPossibleSolutionCheckPopup(
    private val binding: ActivityMainBinding,
    private val resources: Resources,
    private val context: Context,
    private val theme: Resources.Theme,
    private val lifecycleOwner: LifecycleOwner,
) {
    private val duration: Long = 4000

    private val balloonHeight = 84

    private val balloonWidth =
        min(
            (binding.mainBottomAppBar.measuredWidth / resources.displayMetrics.density * 0.8).toInt(),
            400,
        )

    private val foregroundColor =
        MaterialColors.getColor(binding.root, R.attr.colorMainHintPopupErrorsForeground)

    private val backgroundColor =
        MaterialColors.getColor(binding.root, R.attr.colorMainHintPopupErrorsBackground)

    fun show(possibleSolution: PossibleSolutionCheckState) {
        val balloon =
            createBalloon(ContextThemeWrapper(context, R.style.BalloonHintPopupTheme)) {
                iconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.alert_outline, theme)
                text =
                    if (possibleSolution == PossibleSolutionCheckState.NishioMayBeChecked) {
                        resources.getString(R.string.game_nishio_check_popup_wrong_solution)
                    } else {
                        resources.getString(R.string.game_check_possible_solution_popup_wrong_solution)
                    }
                textSize = 14f
                setBackgroundColor(this@BalloonPossibleSolutionCheckPopup.backgroundColor)
                setTextColor(foregroundColor)
                setIconColor(foregroundColor)
                setWidth(balloonWidth)
                setHeight(balloonHeight)
                setIsVisibleArrow(false)
                paddingLeft = 16
                paddingRight = 16 + iconWidth + iconSpace
                setCornerRadius(8f)
                setBalloonAnimation(BalloonAnimation.NONE)

                autoDismissDuration = duration
                setDismissWhenClicked(true)
                setDismissWhenLifecycleOnPause(true)
                dismissWhenTouchOutside = true
                setFocusable(false)

                setLifecycleOwner(this@BalloonPossibleSolutionCheckPopup.lifecycleOwner)
                build()
            }

        balloon.showAlignTop(binding.possibleSolutionCheckFab)
    }
}
