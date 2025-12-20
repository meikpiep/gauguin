package org.piepmeyer.gauguin.ui.main

import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import org.piepmeyer.gauguin.databinding.ActivityMainBinding

class MainActivityInsets(
    private val binding: ActivityMainBinding,
    private val layoutTagMainActivity: String,
) {
    fun initializeInsets() {
        val gridViewNeedsTopPadding = layoutTagMainActivity.contains("grid-view-top-padding")
        val gridViewNeedsBottomPadding = layoutTagMainActivity.contains("grid-view-bottom-padding")
        val gridViewNeedsStartPadding = layoutTagMainActivity.contains("grid-view-start-padding")
        val gridViewNeedsEndPadding = layoutTagMainActivity.contains("grid-view-end-padding")

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

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.mainBottomAppBar,
        ) { v, insets ->
            val innerPadding =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout(),
                )

            val right = binding.gridview.right
            val useMarginOfGridView = v.marginStart != 0 && right > 0

            val additionalLeftPadding =
                if (useMarginOfGridView) {
                    right
                } else {
                    0
                }

            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = innerPadding.left + additionalLeftPadding
                rightMargin = innerPadding.right
                bottomMargin = innerPadding.bottom
            }

            WindowInsetsCompat.CONSUMED
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
}
