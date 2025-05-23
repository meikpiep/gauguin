package org.piepmeyer.gauguin.ui.main

import android.app.Activity
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.color.MaterialColors
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.OnBalloonDismissListener
import com.skydoves.balloon.createBalloon
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.ui.ActivityUtils

class ThemeChooserBalloon(
    private val mainActivity: Activity,
) : KoinComponent {
    private val activityUtils: ActivityUtils by inject()

    fun showBalloon(
        inflater: LayoutInflater,
        parent: ViewGroup,
        lifecycleOwner: LifecycleOwner,
        anchorView: View,
    ) {
        val fragment = ThemeChooserFragment(mainActivity)

        val view = fragment.onCreateView(inflater, parent, null)

        val context =
            ContextThemeWrapper(
                mainActivity.baseContext,
                activityUtils.theme(mainActivity.baseContext),
            )

        val balloon =
            createBalloon(context) {
                setLayout(view)
                setWidth(BalloonSizeSpec.WRAP)
                setHeight(BalloonSizeSpec.WRAP)
                setBackgroundColor(
                    MaterialColors.getColor(context, com.google.android.material.R.attr.colorSurfaceVariant, ""),
                )
                setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                setArrowSize(10)
                setArrowPosition(0.5f)
                setPadding(8)
                paddingBottom = 16
                setCornerRadius(8f)
                setBalloonAnimation(
                    if (MainNavigationViewService.isShowingThemeChooser) {
                        BalloonAnimation.NONE
                    } else {
                        BalloonAnimation.FADE
                    },
                )
                onBalloonDismissListener =
                    OnBalloonDismissListener {
                        if (!fragment.themeHasBeenAltered()) {
                            MainNavigationViewService.isShowingThemeChooser = false
                        }
                    }

                setLifecycleOwner(lifecycleOwner)

                build()
            }

        MainNavigationViewService.isShowingThemeChooser = true

        balloon.showAlignBottom(anchorView)
    }
}
