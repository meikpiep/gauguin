package com.holokenmod;

import java.util.HashMap;
import java.util.Map;

public class GridCellBorders {
	private final Map<Direction, GridBorderType> borders = new HashMap<>();
	
	public GridCellBorders() {
		this(GridBorderType.BORDER_NONE,
				GridBorderType.BORDER_NONE,
				GridBorderType.BORDER_NONE,
				GridBorderType.BORDER_NONE);
	}
	
	public GridCellBorders(final GridBorderType north, final GridBorderType east, final GridBorderType south, final GridBorderType west) {
		borders.put(Direction.NORTH, north);
		borders.put(Direction.WEST, west);
		borders.put(Direction.SOUTH, south);
		borders.put(Direction.EAST, east);
	}
	
	public GridBorderType getBorderType(final Direction direction) {
		return borders.get(direction);
	}
	
	void setBorderType(final Direction direction, final GridBorderType borderType) {
		borders.put(direction, borderType);
	}
}
