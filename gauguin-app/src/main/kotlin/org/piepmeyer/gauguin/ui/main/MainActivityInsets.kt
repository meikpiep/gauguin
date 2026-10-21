package org.piepmeyer.gauguin.ui.main

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import org.piepmeyer.gauguin.databinding.ActivityMainBinding

class MainActivityInsets(
    private val binding: ActivityMainBinding,
) {
    private data class MainAppBarLayoutHints(
        val startAtEndOfGridView: Boolean,
        val innerPaddingLeft: Int,
    )

    private var mainAppBarHints: MainAppBarLayoutHints = MainAppBarLayoutHints(false, 0)

    fun initializeInsets() {
        val gridViewNeedsTopPadding = (binding.gridview.tag as? String)?.contains("top-padding") ?: false
        val gridViewNeedsBottomPadding = (binding.gridview.tag as? String)?.contains("bottom-padding") ?: false
        val gridViewNeedsStartPadding = (binding.gridview.tag as? String)?.contains("start-padding") ?: false
        val gridViewNeedsEndPadding = (binding.gridview.tag as? String)?.contains("end-padding") ?: false

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.gameTopFrame,
        ) { v, insets ->
            val innerPadding =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout(),
                )
            v.setPadding(
                innerPadding.left,
                innerPadding.top,
                innerPadding.right,
                0,
            )

            WindowInsetsCompat.CONSUMED
        }

        if (gridViewNeedsTopPadding || gridViewNeedsBottomPadding || gridViewNeedsStartPadding || gridViewNeedsEndPadding) {
            ViewCompat.setOnApplyWindowInsetsListener(
                binding.gridview,
            ) { v, insets ->
                val innerPadding =
                    insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                            or WindowInsetsCompat.Type.displayCutout(),
                    )
                v.setPadding(
                    if (gridViewNeedsStartPadding) innerPadding.left else 0,
                    if (gridViewNeedsTopPadding) innerPadding.top else 0,
                    if (gridViewNeedsEndPadding) innerPadding.right else 0,
                    if (gridViewNeedsBottomPadding) innerPadding.bottom else 0,
                )

                WindowInsetsCompat.CONSUMED
            }
        }

        mainAppBarHints =
            MainAppBarLayoutHints(
                startAtEndOfGridView = binding.mainBottomAppBar.marginStart != 0,
                innerPaddingLeft = 0,
            )

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.mainBottomAppBar,
        ) { v, insets ->
            val innerPadding =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout(),
                )

            mainAppBarHints =
                mainAppBarHints.copy(
                    innerPaddingLeft = innerPadding.left,
                )

            updateMainAppBarLayout(v)

            WindowInsetsCompat.CONSUMED
        }

        binding.mainBottomAppBar.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            updateMainAppBarLayout(v)
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.keypadFrame,
        ) { v, insets ->
            val innerPadding =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout(),
                )
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = innerPadding.left
                rightMargin = innerPadding.right
                bottomMargin = innerPadding.bottom
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun updateMainAppBarLayout(v: View) {
        if (!mainAppBarHints.startAtEndOfGridView) {
            return
        }

        val right = binding.gridview.right

        val additionalLeftPadding =
            if (right > 0) {
                right
            } else {
                0
            }

        v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = mainAppBarHints.innerPaddingLeft + additionalLeftPadding
        }
    }
}
