package com.tortuca.holoken;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.WindowManager;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
	    if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("showfullscreen", false))
	    	this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    else
	    	this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	  
      // Deprecated addPreferencesFromResources, use fragments instead?
      addPreferencesFromResource(R.xml.activity_settings);
      
   	 Preference statsPref = findPreference("holokenstats"); 
   	 statsPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
   		public boolean onPreferenceClick(Preference preference) {
        	startActivity(new Intent(getApplicationContext(), StatsActivity.class));
 			return true;
   		}
   	 });
   	 
  	 Preference ratePref = findPreference("rateapp"); 
  	 ratePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
  		public boolean onPreferenceClick(Preference preference) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			try {
				intent.setData(Uri.parse("market://details?id=com.tortuca.holoken"));
				startActivity(intent);
				return true;
			}
			catch (Exception e) {
				intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=com.tortuca.holoken"));
				startActivity(intent);
				return false;
			}
  		}
  	 });
  	 
  	 Preference reportBugs = findPreference("reportbugs"); 
  	 reportBugs.setOnPreferenceClickListener(new OnPreferenceClickListener() {
  		public boolean onPreferenceClick(Preference preference) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("http://code.google.com/p/holoken/issues/list"));
			startActivity(intent);
			return true;
  		}
  	 });
  }
  
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

  }
}
