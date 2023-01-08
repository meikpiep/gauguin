package com.holokenmod.ui.grid;

public class GridCellSizeService {
	private static final GridCellSizeService INSTANCE = new GridCellSizeService();
	
	private int cellSizePercent = 100;
	private GridCellSizeListener listener = null;
	
	public static GridCellSizeService getInstance() {
		return INSTANCE;
	}
	
	public int getCellSizePercent() {
		return cellSizePercent;
	}
	
	public void setCellSizePercent(int cellSizePercent) {
		this.cellSizePercent = cellSizePercent;
		
		if (listener != null) {
			listener.cellSizeChanged(cellSizePercent);
		}
	}
	
	public void setCellSizeListener(GridCellSizeListener listener) {
		this.listener = listener;
	}
}
