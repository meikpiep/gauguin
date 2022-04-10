package com.holokenmod.options;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class GameVariant {
	private static final GameVariant INSTANCE = new GameVariant();
	
	private boolean showOperators;
	private GridCageOperation cageOperation;
	private DigitSetting digitSetting;
	private SingleCageUsage singleCageUsage;
	
	public static GameVariant getInstance() {
		return INSTANCE;
	}
	
	public GameVariant copy() {
		GameVariant copy = new GameVariant();
		
		copy.showOperators = this.showOperators;
		copy.cageOperation = this.cageOperation;
		copy.digitSetting = this.digitSetting;
		copy.singleCageUsage = this.singleCageUsage;
		
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
		
		GameVariant that = (GameVariant) o;
		
		return new EqualsBuilder()
				.append(showOperators, that.showOperators)
				.append(cageOperation, that.cageOperation)
				.append(digitSetting, that.digitSetting)
				.append(singleCageUsage, that.singleCageUsage)
				.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(showOperators)
				.append(cageOperation)
				.append(digitSetting)
				.append(singleCageUsage)
				.toHashCode();
	}
	
	public void setShowOperators(final boolean showOperators) {
		this.showOperators = showOperators;
	}
	
	public boolean showOperators() {
		return showOperators;
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
	
	public void loadPreferences(ApplicationPreferences preferences) {
		showOperators = preferences.showOperators();
		cageOperation = preferences.getOperations();
		digitSetting = preferences.getDigitSetting();
		singleCageUsage = preferences.getSingleCageUsage();
	}
}