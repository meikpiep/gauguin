package org.piepmeyer.gauguin.ui.newgame

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.creation.GridCalculatorFactory
import org.piepmeyer.gauguin.databinding.FragmentNewGameGridShapeOptionsBinding
import org.piepmeyer.gauguin.preferences.ApplicationPreferences
import kotlin.math.min
import kotlin.math.roundToInt

class GridShapeOptionsFragment :
    Fragment(R.layout.fragment_new_game_grid_shape_options),
    KoinComponent {
    private val applicationPreferences: ApplicationPreferences by inject()
    private lateinit var viewModel: NewGameViewModel
    private var squareOnlyMode = false
    private lateinit var binding: FragmentNewGameGridShapeOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNewGameGridShapeOptionsBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.newGridPreview.isPreviewMode = true
        binding.newGridPreview.updateTheme()
        squareOnlyMode = applicationPreferences.squareOnlyGrid

        binding.squareRectangularToggleGroup.check(
            if (squareOnlyMode) {
                binding.squareButton.id
            } else {
                binding.rectangularButton.id
            },
        )
        binding.squareRectangularToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                squareOnlyChanged(checkedId == binding.squareButton.id)
            }
        }

        viewModel = ViewModelProvider(requireActivity()).get(NewGameViewModel::class.java)

        if (resources.getBoolean(R.bool.debuggable)) {
            binding.widthslider.valueFrom = 2f
            binding.heigthslider.valueFrom = 2f

            binding.newGameNewAlgorithmSwitch.visibility = View.VISIBLE
            binding.newGameNewAlgorithmSwitch.isChecked = applicationPreferences.mergingCageAlgorithm
            binding.newGameNewAlgorithmSwitch.setOnCheckedChangeListener { _, isChecked ->
                applicationPreferences.mergingCageAlgorithm = isChecked
                GridCalculatorFactory.alwaysUseNewAlgorithm = isChecked
                viewModel.clearGrids()
            }
        }

        binding.widthslider.value = applicationPreferences.gridWidth.toFloat()
        binding.heigthslider.value = applicationPreferences.gridHeigth.toFloat()
        binding.widthslider.addOnChangeListener(
            Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
                sizeSliderChanged(
                    value,
                )
            },
        )
        binding.heigthslider.addOnChangeListener(
            Slider.OnChangeListener { _: Slider?, value: Float, _: Boolean ->
                sizeSliderChanged(
                    value,
                )
            },
        )
        setVisibilityOfHeightSlider(false)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.previewGridState.collect {
                    previewGridCalculated(it)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.gameVariantState.collect {
                    updateGridSizeLabel()
                }
            }
        }
    }

    private fun updateGridSizeLabel() {
        val variant = viewModel.gameVariantState.value.variant

        binding.newGameGridSize.text =
            if (squareOnlyMode) {
                resources.getString(R.string.game_setting_new_grid_shape_size_square, variant.width)
            } else {
                resources.getString(R.string.game_setting_new_grid_shape_size_rectangular, variant.width, variant.height)
            }
    }

    private fun sizeSliderChanged(value: Float) {
        if (squareOnlyMode) {
            binding.widthslider.value = value
            binding.heigthslider.value = value
        }
        applicationPreferences.gridWidth = binding.widthslider.value.roundToInt()
        applicationPreferences.gridHeigth = binding.heigthslider.value.roundToInt()

        viewModel.calculateGrid()
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
        updateGridSizeLabel()
        setVisibilityOfHeightSlider(animate = true)
    }

    private fun setVisibilityOfHeightSlider(animate: Boolean) {
        if (animate) {
            TransitionManager.beginDelayedTransition(binding.root)
        }

        if (squareOnlyMode) {
            binding.heigthslider.visibility = View.GONE
        } else {
            binding.heigthslider.visibility = View.VISIBLE
        }
    }

    private fun previewGridCalculated(gridPreview: GridPreviewState) {
        binding.newGridPreview.let {
            it.visibility =
                if (gridPreview.grid != null) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            if (gridPreview.grid != null) {
                it.grid = gridPreview.grid
                it.rebuildCellsFromGrid()
                it.updateTheme()
                it.setPreviewStillCalculating(gridPreview.calculationState == GridCalculationState.STILL_CALCULATING)
                it.invalidate()
            }
        }
    }
}
