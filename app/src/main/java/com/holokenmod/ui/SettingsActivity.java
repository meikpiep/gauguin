package com.holokenmod.ui;

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

import com.holokenmod.R;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("showfullscreen", false))
            this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Deprecated addPreferencesFromResources, use fragments instead?
        addPreferencesFromResource(R.xml.activity_settings);

        final Preference ratePref = findPreference("rateapp");
        ratePref.setOnPreferenceClickListener(preference -> {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            try {
                intent.setData(Uri.parse("market://details?id=" ));
                startActivity(intent);
                return true;
            }
            catch (final Exception e) {
                intent.setData(Uri.parse("http://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName()));
                startActivity(intent);
                return false;
            }
        });

        final Preference reportBugs = findPreference("reportbugs");
        reportBugs.setOnPreferenceClickListener(preference -> {
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://github.com/queler/holokenmod/issues"));
            startActivity(intent);
            return true;
        });
    }

    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {

    }
}
