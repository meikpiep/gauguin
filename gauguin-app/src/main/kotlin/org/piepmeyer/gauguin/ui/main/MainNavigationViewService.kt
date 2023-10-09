package org.piepmeyer.gauguin.ui.main

import android.view.ViewGroup
import androidx.core.view.marginStart
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.slider.Slider
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.ActivityMainBinding
import org.piepmeyer.gauguin.ui.grid.GridCellSizeService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.roundToInt

class MainNavigationViewService(
    private val mainActivity: MainActivity,
    private val binding: ActivityMainBinding,
): KoinComponent {
    private val cellSizeService: GridCellSizeService by inject()

    fun initialize() {
        binding.container.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.mainNavigationView.setNavigationItemSelectedListener(MainNavigationItemSelectedListener(mainActivity))

        val gridScaleSlider =
            binding.mainNavigationView.getHeaderView(0).findViewById<Slider>(R.id.gridScaleSlider)
        gridScaleSlider.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, fromUser: Boolean ->
            if (fromUser) {
                cellSizeService.cellSizePercent = value.roundToInt()
            }
        })

        cellSizeService.cellSizePercent = 100
        gridScaleSlider.value = cellSizeService.cellSizePercent.toFloat()

        binding.mainBottomAppBar.setOnMenuItemClickListener(
            BottomAppBarItemClickListener(
                binding.mainConstraintLayout,
                mainActivity)
        )
        binding.mainBottomAppBar.setNavigationOnClickListener { binding.container.open() }

        binding.gridview.addOnLayoutChangeListener { _, _, _, right, _, _, _, _, _ ->
            if (binding.mainBottomAppBar.marginStart != 0 && right > 0 && binding.mainBottomAppBar.marginStart != right) {
                val marginParams =
                    binding.mainBottomAppBar.layoutParams as ViewGroup.MarginLayoutParams
                marginParams.marginStart = right

                binding.mainBottomAppBar.updateLayoutParams<ViewGroup.MarginLayoutParams> {  }
                binding.container.invalidate()
            }
        }
    }
}
