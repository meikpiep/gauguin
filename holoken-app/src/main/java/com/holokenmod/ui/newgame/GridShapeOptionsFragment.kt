package com.holokenmod.ui.newgame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider
import com.holokenmod.R
import com.holokenmod.databinding.NewGameGridShapeOptionsFragmentBinding
import com.holokenmod.grid.Grid
import com.holokenmod.options.ApplicationPreferences
import com.holokenmod.ui.grid.GridUI
import kotlin.math.min
import kotlin.math.roundToInt

class GridShapeOptionsFragment : Fragment(R.layout.new_game_grid_shape_options_fragment) {
    private var gridPreviewHolder: GridPreviewHolder? = null
    private var squareOnlyMode = false
    private var grid: Grid? = null
    private var binding: NewGameGridShapeOptionsFragmentBinding? = null
    fun setGridPreviewHolder(gridPreviewHolder: GridPreviewHolder?) {
        this.gridPreviewHolder = gridPreviewHolder
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewGameGridShapeOptionsFragmentBinding.inflate(inflater, parent, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding!!.newGridPreview.isPreviewMode = true
        binding!!.newGridPreview.updateTheme()
        if (grid != null) {
            updateGridPreview(grid!!)
        }
        squareOnlyMode = ApplicationPreferences.instance.squareOnlyGrid
        binding!!.rectChip.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            squareOnlyChanged(
                !isChecked
            )
        }
        binding!!.widthslider.value =
            ApplicationPreferences.instance.gridWidth.toFloat()
        binding!!.heigthslider.value =
            ApplicationPreferences.instance.gridHeigth.toFloat()
        binding!!.widthslider.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            sizeSliderChanged(
                value
            )
        })
        binding!!.heigthslider.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            sizeSliderChanged(
                value
            )
        })
        setVisibilityOfHeightSlider()
    }

    private fun sizeSliderChanged(value: Float) {
        if (squareOnlyMode) {
            binding!!.widthslider.value = value
            binding!!.heigthslider.value = value
        }
        ApplicationPreferences.instance
            .gridWidth = binding!!.widthslider.value.roundToInt()
        ApplicationPreferences.instance
            .gridHeigth = binding!!.heigthslider.value.roundToInt()
        gridPreviewHolder!!.refreshGrid()
    }

    private fun squareOnlyChanged(isChecked: Boolean) {
        squareOnlyMode = isChecked
        ApplicationPreferences.instance.squareOnlyGrid = isChecked
        if (squareOnlyMode) {
            val squareSize = min(binding!!.widthslider.value, binding!!.heigthslider.value)
            binding!!.widthslider.value = squareSize
            binding!!.heigthslider.value = squareSize
            ApplicationPreferences.instance
                .gridWidth = binding!!.widthslider.value.roundToInt()
            ApplicationPreferences.instance.gridHeigth = binding!!.heigthslider.value.roundToInt()
        }
        setVisibilityOfHeightSlider()
    }

    private fun setVisibilityOfHeightSlider() {
        if (squareOnlyMode) {
            binding!!.heigthslider.visibility = View.INVISIBLE
        } else {
            binding!!.heigthslider.visibility = View.VISIBLE
        }
    }

    val gridUI: GridUI?
        get() = if (binding == null) {
            null
        } else binding!!.newGridPreview

    fun setGrid(grid: Grid) {
        this.grid = grid
        if (binding != null) {
            updateGridPreview(grid)
        }
    }

    private fun updateGridPreview(grid: Grid) {
        binding!!.newGridPreview.grid = grid
        binding!!.newGridPreview.rebuildCellsFromGrid()
        binding!!.newGridPreview.invalidate()
        binding!!.newGameGridSize.text =
            "${grid!!.gridSize.width} x ${grid.gridSize.height}"
    }
}