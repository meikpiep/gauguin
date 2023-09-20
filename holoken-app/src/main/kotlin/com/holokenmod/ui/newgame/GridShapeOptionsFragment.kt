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
import com.holokenmod.options.ApplicationPreferencesImpl
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.min
import kotlin.math.roundToInt

class GridShapeOptionsFragment : Fragment(R.layout.new_game_grid_shape_options_fragment), KoinComponent {
    private val applicationPreferences: ApplicationPreferencesImpl by inject()
    private var gridPreviewHolder: GridPreviewHolder? = null
    private var squareOnlyMode = false
    private var grid: Grid? = null
    private lateinit var binding: NewGameGridShapeOptionsFragmentBinding
            
    fun setGridPreviewHolder(gridPreviewHolder: GridPreviewHolder) {
        this.gridPreviewHolder = gridPreviewHolder
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewGameGridShapeOptionsFragmentBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.newGridPreview.isPreviewMode = true
        binding.newGridPreview.updateTheme()
        grid?.let {
            updateGridPreview(it)
        }
        squareOnlyMode = applicationPreferences.squareOnlyGrid
        binding.rectChip.isChecked = !squareOnlyMode
        binding.rectChip.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            squareOnlyChanged(
                !isChecked
            )
        }
        binding.widthslider.value = applicationPreferences.gridWidth.toFloat()
        binding.heigthslider.value = applicationPreferences.gridHeigth.toFloat()
        binding.widthslider.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            sizeSliderChanged(
                value
            )
        })
        binding.heigthslider.addOnChangeListener(Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
            sizeSliderChanged(
                value
            )
        })
        setVisibilityOfHeightSlider()
    }

    private fun sizeSliderChanged(value: Float) {
        if (squareOnlyMode) {
            binding.widthslider.value = value
            binding.heigthslider.value = value
        }
        applicationPreferences.gridWidth = binding.widthslider.value.roundToInt()
        applicationPreferences.gridHeigth = binding.heigthslider.value.roundToInt()
        gridPreviewHolder!!.refreshGrid()
    }

    private fun squareOnlyChanged(isChecked: Boolean) {
        squareOnlyMode = isChecked
        applicationPreferences.squareOnlyGrid = isChecked
        if (squareOnlyMode) {
            val squareSize = min(binding.widthslider.value, binding.heigthslider.value)
            binding.widthslider.value = squareSize
            binding.heigthslider.value = squareSize
            applicationPreferences.gridWidth = binding.widthslider.value.roundToInt()
            applicationPreferences.gridHeigth = binding.heigthslider.value.roundToInt()
        }
        setVisibilityOfHeightSlider()
    }

    private fun setVisibilityOfHeightSlider() {
        if (squareOnlyMode) {
            binding.heigthslider.visibility = View.GONE
        } else {
            binding.heigthslider.visibility = View.VISIBLE
        }
    }

    fun setGrid(grid: Grid) {
        this.grid = grid

        if (this.isAdded)
            updateGridPreview(grid)
    }

    private fun updateGridPreview(grid: Grid) {
        binding.newGridPreview.grid = grid
        binding.newGridPreview.rebuildCellsFromGrid()
        binding.newGridPreview.invalidate()
        binding.newGameGridSize.text =
            "${grid.gridSize.width} x ${grid.gridSize.height}"
    }

    fun previewGridCalculated(grid: Grid) {
        this.grid = grid

        if (this.isAdded) {
            binding.newGridPreview.let {
                it.grid = grid
                it.rebuildCellsFromGrid()
                it.updateTheme()
                it.setPreviewStillCalculating(false)
                it.invalidate()
            }
        }
    }

    fun updateGridUI(previewStillCalculating: Boolean) {
        if (this.isAdded) {
            binding.newGridPreview.let {
                it.setPreviewStillCalculating(previewStillCalculating)
                it.rebuildCellsFromGrid()
                it.updateTheme()
                it.invalidate()
            }
        }
    }
}