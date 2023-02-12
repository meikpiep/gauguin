package com.holokenmod.options;

import android.content.SharedPreferences;

import com.holokenmod.Theme;

import org.apache.commons.lang3.EnumUtils;

public class ApplicationPreferences {
	private static final ApplicationPreferences INSTANCE = new ApplicationPreferences();
	
	private SharedPreferences preferences;
	
	public static ApplicationPreferences getInstance() {
		return INSTANCE;
	}
	
	public Theme getTheme() {
		final String themePref = this.preferences.getString("theme", Theme.LIGHT.name());
		return EnumUtils.getEnum(Theme.class, themePref, Theme.LIGHT);
	}
	
	public void setPreferenceManager(final SharedPreferences preferences) {
		this.preferences = preferences;
	}
	
	public boolean showDupedDigits() {
		return preferences.getBoolean("duplicates", true);
	}
	
	public boolean showBadMaths() {
		return preferences.getBoolean("badmaths", true);
	}
	
	public boolean showOperators() {
		return preferences.getBoolean("showOperators", true);
	}
	
	public void setShowOperators(boolean showOperators) {
		preferences
				.edit()
				.putBoolean("showOperators", showOperators)
				.commit();
	}
	
	public boolean removePencils() {
		return preferences.getBoolean("removepencils", false);
	}
	
	public SharedPreferences getPrefereneces() {
		return preferences;
	}
	
	public GridCageOperation getOperations() {
		final String operations = preferences
				.getString("operations", GridCageOperation.OPERATIONS_ALL.name());
		return EnumUtils.getEnum(GridCageOperation.class, operations, GridCageOperation.OPERATIONS_ALL);
	}
	
	public void setOperations(GridCageOperation operations) {
		preferences
				.edit()
				.putString("operations", operations.name())
				.commit();
	}
	
	public SingleCageUsage getSingleCageUsage() {
		final String usage = preferences
				.getString("singlecages", SingleCageUsage.FIXED_NUMBER.name());
		return SingleCageUsage.valueOf(usage);
	}
	
	public void setSingleCageUsage(SingleCageUsage singleCageUsage) {
		preferences
				.edit()
				.putString("singlecages", singleCageUsage.name())
				.commit();
	}
	
	public DifficultySetting getDifficultySetting() {
		final String usage = preferences.getString("difficulty", DifficultySetting.ANY.name());
		
		return EnumUtils.getEnum(DifficultySetting.class, usage, DifficultySetting.ANY);
	}
	
	public void setDifficultySetting(DifficultySetting difficultySetting) {
		preferences
				.edit()
				.putString("difficulty", difficultySetting.name())
				.commit();
	}
	
	public DigitSetting getDigitSetting() {
		final String usage = preferences.getString("digits", DigitSetting.FIRST_DIGIT_ONE.name());
		
		return EnumUtils.getEnum(DigitSetting.class, usage, DigitSetting.FIRST_DIGIT_ONE);
	}
	
	public void setDigitSetting(DigitSetting digitSetting) {
		preferences
				.edit()
				.putString("digits", digitSetting.name())
				.commit();
	}
	
	public boolean show3x3Pencils() {
		return preferences.getBoolean("pencil3x3", true);
	}
	
	public boolean newUserCheck() {
		final boolean new_user = preferences.getBoolean("newuser", true);
		
		if (new_user) {
			final SharedPreferences.Editor prefeditor = preferences.edit();
			prefeditor.putBoolean("newuser", false);
			prefeditor.commit();
		}
		
		return new_user;
	}
	
	public int getGridWidth() {
		return preferences.getInt("gridWidth", 6);
	}
	
	public void setGridWidth(int width) {
		preferences
				.edit()
				.putInt("gridWidth", width)
				.commit();
	}
	
	public int getGridHeigth() {
		return preferences.getInt("gridHeigth", 6);
	}
	
	public void setGridHeigth(int heigth) {
		preferences
				.edit()
				.putInt("gridHeigth", heigth)
				.commit();
	}
	
	public boolean getSquareOnlyGrid() {
		return preferences.getBoolean("squareOnlyGrid", true);
	}
	
	public void setSquareOnlyGrid(boolean squareOnly) {
		preferences
				.edit()
				.putBoolean("squareOnlyGrid", squareOnly)
				.commit();
	}
	
	public void loadGameVariant() {
		GameOptionsVariant gameVariant = CurrentGameOptionsVariant.getInstance();
		
		loadIntoGameVariant(gameVariant);
	}
	
	public GameOptionsVariant getGameVariant() {
		GameOptionsVariant gameVariant = new GameOptionsVariant();
		
		loadIntoGameVariant(gameVariant);
		
		return gameVariant;
	}
	
	public void loadIntoGameVariant(GameOptionsVariant gameVariant) {
		gameVariant.setShowOperators(this.showOperators());
		gameVariant.setCageOperation(this.getOperations());
		gameVariant.setDigitSetting(this.getDigitSetting());
		gameVariant.setSingleCageUsage(this.getSingleCageUsage());
		gameVariant.setShowBadMaths(this.showBadMaths());
		gameVariant.setDifficultySetting(this.getDifficultySetting());
	}
}