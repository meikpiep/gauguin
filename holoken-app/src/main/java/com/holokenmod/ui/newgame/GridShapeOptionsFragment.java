package com.holokenmod.ui.newgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.holokenmod.R;
import com.holokenmod.databinding.NewGameGridShapeOptionsFragmentBinding;
import com.holokenmod.grid.Grid;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.ui.grid.GridUI;

public class GridShapeOptionsFragment extends Fragment {
	
	private GridPreviewHolder gridPreviewHolder;
	
	private boolean squareOnlyMode = false;
	private Grid grid;
	
	private NewGameGridShapeOptionsFragmentBinding binding;
	
	public GridShapeOptionsFragment() {
		super(R.layout.new_game_grid_shape_options_fragment);
		
	}
	
	void setGridPreviewHolder(GridPreviewHolder gridPreviewHolder) {
		this.gridPreviewHolder = gridPreviewHolder;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		binding = NewGameGridShapeOptionsFragmentBinding.inflate(inflater, parent, false);
		
		return binding.getRoot();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}
	
	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		binding.newGridPreview.setPreviewMode(true);
		binding.newGridPreview.updateTheme();
		
		if (grid != null) {
			updateGridPreview(grid);
		}
		
		squareOnlyMode = ApplicationPreferences.getInstance().getSquareOnlyGrid();
		
		binding.rectChip.setOnCheckedChangeListener((buttonView, isChecked) -> squareOnlyChanged(!isChecked));
		
		binding.widthslider.setValue(ApplicationPreferences.getInstance().getGridWidth());
		binding.heigthslider.setValue(ApplicationPreferences.getInstance().getGridHeigth());
		
		binding.widthslider.addOnChangeListener((slider, value,  fromUser) -> sizeSliderChanged(value));
		binding.heigthslider.addOnChangeListener((slider, value,  fromUser) -> sizeSliderChanged(value));
		
		setVisibilityOfHeightSlider();
		
	}
	
	private void sizeSliderChanged(float value) {
		if (squareOnlyMode) {
			binding.widthslider.setValue(value);
			binding.heigthslider.setValue(value);
		}
		
		ApplicationPreferences.getInstance().setGridWidth(Math.round(binding.widthslider.getValue()));
		ApplicationPreferences.getInstance().setGridHeigth(Math.round(binding.heigthslider.getValue()));
		
		gridPreviewHolder.refreshGrid();
	}
	
	private void squareOnlyChanged(boolean isChecked) {
		squareOnlyMode = isChecked;
		ApplicationPreferences.getInstance().setSquareOnlyGrid(isChecked);
		
		if (squareOnlyMode) {
			float squareSize = Math.min(binding.widthslider.getValue(), binding.heigthslider.getValue());
			
			binding.widthslider.setValue(squareSize);
			binding.heigthslider.setValue(squareSize);
			
			ApplicationPreferences.getInstance().setGridWidth(Math.round(binding.widthslider.getValue()));
			ApplicationPreferences.getInstance().setGridHeigth(Math.round(binding.heigthslider.getValue()));
		}
		
		setVisibilityOfHeightSlider();
	}
	
	private void setVisibilityOfHeightSlider() {
		if (squareOnlyMode) {
			binding.heigthslider.setVisibility(View.INVISIBLE);
		} else {
			binding.heigthslider.setVisibility(View.VISIBLE);
		}
	}
	
	GridUI getGridUI() {
		if (binding == null) {
			return null;
		}
		
		return binding.newGridPreview;
	}
	
	public void setGrid(Grid grid) {
		this.grid = grid;
		
		if (binding != null) {
			updateGridPreview(grid);
		}
	}
	
	private void updateGridPreview(Grid grid) {
		binding.newGridPreview.setGrid(grid);
		binding.newGridPreview.rebuildCellsFromGrid();
		binding.newGridPreview.invalidate();
		
		binding.newGameGridSize.setText(grid.getGridSize().getWidth() + " x " + grid.getGridSize().getHeight());
	}
}
