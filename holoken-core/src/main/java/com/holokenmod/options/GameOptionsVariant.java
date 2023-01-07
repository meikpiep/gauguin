package com.holokenmod.options;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class GameOptionsVariant {
	private boolean showOperators;
	private GridCageOperation cageOperation;
	private DigitSetting digitSetting;
	private SingleCageUsage singleCageUsage;
	private boolean showBadMaths;
	
	public static GameOptionsVariant createClassic() {
		return createClassic(DigitSetting.FIRST_DIGIT_ONE);
	}
	
	public static GameOptionsVariant createClassic(DigitSetting digitSetting) {
		GameOptionsVariant variant = new GameOptionsVariant();
		
		variant.cageOperation = GridCageOperation.OPERATIONS_ALL;
		variant.showOperators = true;
		variant.digitSetting = digitSetting;
		variant.singleCageUsage = SingleCageUsage.FIXED_NUMBER;
		variant.showBadMaths = true;
		
		return variant;
	}
	
	public GameOptionsVariant copy() {
		GameOptionsVariant copy = new GameOptionsVariant();
		
		copy.showOperators = this.showOperators;
		copy.cageOperation = this.cageOperation;
		copy.digitSetting = this.digitSetting;
		copy.singleCageUsage = this.singleCageUsage;
		copy.showBadMaths = this.showBadMaths;
		
		return copy;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		
		GameOptionsVariant that = (GameOptionsVariant) o;
		
		return new EqualsBuilder()
				.append(showOperators, that.showOperators)
				.append(cageOperation, that.cageOperation)
				.append(digitSetting, that.digitSetting)
				.append(singleCageUsage, that.singleCageUsage)
				.append(showBadMaths, that.showBadMaths)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(showOperators)
				.append(cageOperation)
				.append(digitSetting)
				.append(singleCageUsage)
				.append(showBadMaths)
				.toHashCode();
	}
	
	public void setShowOperators(final boolean showOperators) {
		this.showOperators = showOperators;
	}
	
	public boolean showOperators() {
		return showOperators;
	}
	
	public void setShowBadMaths(final boolean showBadMaths) {
		this.showBadMaths = showBadMaths;
	}
	
	public boolean showBadMaths() {
		return showBadMaths;
	}
	
	public GridCageOperation getCageOperation() {
		return this.cageOperation;
	}
	
	public void setCageOperation(GridCageOperation cageOperation) {
		this.cageOperation = cageOperation;
	}
	
	public SingleCageUsage getSingleCageUsage() {
		return this.singleCageUsage;
	}

	public void setSingleCageUsage(SingleCageUsage singleCageUsage) {
		this.singleCageUsage = singleCageUsage;
	}
	
	public DigitSetting getDigitSetting() {
		return this.digitSetting;
	}
	
	public void setDigitSetting(DigitSetting digitSetting) {
		this.digitSetting = digitSetting;
	}
}