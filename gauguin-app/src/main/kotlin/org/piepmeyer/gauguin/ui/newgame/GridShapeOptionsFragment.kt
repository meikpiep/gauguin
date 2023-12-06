package org.piepmeyer.gauguin.ui.newgame

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentNewGameGridShapeOptionsBinding
import org.piepmeyer.gauguin.grid.Grid
import org.piepmeyer.gauguin.options.NumeralSystem
import org.piepmeyer.gauguin.preferences.ApplicationPreferencesImpl
import kotlin.math.min
import kotlin.math.roundToInt

class GridShapeOptionsFragment : Fragment(R.layout.fragment_new_game_grid_shape_options), KoinComponent {
    private val applicationPreferences: ApplicationPreferencesImpl by inject()
    private var gridPreviewHolder: GridPreviewHolder? = null
    private var squareOnlyMode = false
    private var grid: Grid? = null
    private lateinit var binding: FragmentNewGameGridShapeOptionsBinding
            
    fun setGridPreviewHolder(gridPreviewHolder: GridPreviewHolder) {
        this.gridPreviewHolder = gridPreviewHolder
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewGameGridShapeOptionsBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.newGridPreview.isPreviewMode = true
        binding.newGridPreview.updateTheme()
        grid?.let {
            updateGridPreview(it)
        }
        squareOnlyMode = applicationPreferences.squareOnlyGrid

        binding.squareRectangularToggleGroup.check(if (squareOnlyMode) {
                binding.squareButton.id
            } else {
                binding.rectangularButton.id
            }
        )
        binding.squareRectangularToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked->
            if (isChecked) {
                squareOnlyChanged(checkedId == binding.squareButton.id)
            }
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
        setVisibilityOfHeightSlider(false)
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

    private fun setVisibilityOfHeightSlider(animate: Boolean = true) {
        if (animate)
            TransitionManager.beginDelayedTransition(binding.root)

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

    fun setNumeralSystem(numeralSystem: NumeralSystem) {
        if (this.isAdded) {
            gridPreviewHolder!!.refreshGrid()
        }
    }

    private fun updateGridPreview(grid: Grid) {
        binding.newGridPreview.grid = grid
        binding.newGridPreview.rebuildCellsFromGrid()
        binding.newGridPreview.invalidate()
        binding.newGameGridSize.text = resources.getString(R.string.new_grid_shape_size).format(
                if (squareOnlyMode) {
                    grid.gridSize.width
                } else {
                    "${grid.gridSize.width} x ${grid.gridSize.height}"
                }
        )
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