package com.holokenmod.ui.main

import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.slider.Slider
import com.holokenmod.R
import com.holokenmod.databinding.ActivityMainBinding
import com.holokenmod.ui.grid.GridCellSizeService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.roundToInt

class MainNavigationViewInitializer(
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

    }

}
