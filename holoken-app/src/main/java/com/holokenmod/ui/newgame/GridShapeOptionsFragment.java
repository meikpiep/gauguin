package com.holokenmod.ui.newgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;
import com.holokenmod.R;
import com.holokenmod.grid.Grid;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.ui.grid.GridUI;

public class GridShapeOptionsFragment extends Fragment {
	
	private GridPreviewHolder gridPreviewHolder;
	
	private Slider widthSlider;
	private Slider heigthSlider;
	private boolean squareOnlyMode = false;
	private GridUI gridUI;
	private MaterialTextView gridSizeLabel;
	private Grid grid;
	
	public GridShapeOptionsFragment() {
		super(R.layout.new_game_grid_shape_options_fragment);
		
	}
	
	void setGridPreviewHolder(GridPreviewHolder gridPreviewHolder) {
		this.gridPreviewHolder = gridPreviewHolder;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.new_game_grid_shape_options_fragment, parent, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		gridUI = view.findViewById(R.id.newGridPreview);
		gridUI.setPreviewMode(true);
		gridUI.updateTheme();
		
		gridSizeLabel = view.findViewById(R.id.newGameGridSize);
		
		if (grid != null) {
			updateGridPreview(grid);
		}
		
		squareOnlyMode = ApplicationPreferences.getInstance().getSquareOnlyGrid();
		
		widthSlider = view.findViewById(R.id.widthslider);
		heigthSlider = view.findViewById(R.id.heigthslider);
		
		Chip rectChip = view.findViewById(R.id.rectChip);
		rectChip.setOnCheckedChangeListener((buttonView, isChecked) -> squareOnlyChanged(!isChecked));
		
		widthSlider.setValue(ApplicationPreferences.getInstance().getGridWidth());
		heigthSlider.setValue(ApplicationPreferences.getInstance().getGridHeigth());
		
		widthSlider.addOnChangeListener((slider, value,  fromUser) -> sizeSliderChanged(value));
		heigthSlider.addOnChangeListener((slider, value,  fromUser) -> sizeSliderChanged(value));
		
		setVisibilityOfHeightSlider();
		
	}
	
	private void sizeSliderChanged(float value) {
		if (squareOnlyMode) {
			widthSlider.setValue(value);
			heigthSlider.setValue(value);
		}
		
		ApplicationPreferences.getInstance().setGridWidth(Math.round(widthSlider.getValue()));
		ApplicationPreferences.getInstance().setGridHeigth(Math.round(heigthSlider.getValue()));
		
		gridPreviewHolder.refreshGrid();
	}
	
	private void squareOnlyChanged(boolean isChecked) {
		squareOnlyMode = isChecked;
		ApplicationPreferences.getInstance().setSquareOnlyGrid(isChecked);
		
		if (squareOnlyMode) {
			float squareSize = Math.min(widthSlider.getValue(), heigthSlider.getValue());
			
			widthSlider.setValue(squareSize);
			heigthSlider.setValue(squareSize);
			
			ApplicationPreferences.getInstance().setGridWidth(Math.round(widthSlider.getValue()));
			ApplicationPreferences.getInstance().setGridHeigth(Math.round(heigthSlider.getValue()));
		}
		
		setVisibilityOfHeightSlider();
	}
	
	private void setVisibilityOfHeightSlider() {
		if (squareOnlyMode) {
			heigthSlider.setVisibility(View.INVISIBLE);
		} else {
			heigthSlider.setVisibility(View.VISIBLE);
		}
	}
	
	GridUI getGridUI() {
		return gridUI;
	}
	
	public void setGrid(Grid grid) {
		this.grid = grid;
		
		if (gridUI != null) {
			updateGridPreview(grid);
		}
	}
	
	private void updateGridPreview(Grid grid) {
		gridUI.setGrid(grid);
		gridUI.rebuildCellsFromGrid();
		gridUI.invalidate();
		
		gridSizeLabel.setText(grid.getGridSize().getWidth() + " x " + grid.getGridSize().getHeight());
	}
}
