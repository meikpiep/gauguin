package com.holokenmod.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.holokenmod.R;
import com.holokenmod.game.CurrentGameSaver;
import com.holokenmod.ui.LoadGameListActivity;
import com.holokenmod.ui.MainDialogs;
import com.holokenmod.ui.SettingsActivity;
import com.holokenmod.ui.StatsActivity;

public class MainNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {
	
	private final MainActivity mainActivity;
	
	MainNavigationItemSelectedListener(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}
	
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.newGame2:
				mainActivity.createNewGame();
				break;
			case R.id.menu_load:
				final Intent i = new Intent(mainActivity, LoadGameListActivity.class);
				mainActivity.startActivityForResult(i, 7);
				break;
			case R.id.menu_save:
				new CurrentGameSaver(mainActivity.getFilesDir()).save();
				break;
			case R.id.menu_restart_game:
				new MainDialogs(mainActivity, mainActivity.getGame()).restartGameDialog();
				break;
			case R.id.menu_stats:
				mainActivity.startActivity(new Intent(mainActivity, StatsActivity.class));
				break;
			case R.id.menu_settings:
				mainActivity.startActivity(new Intent(mainActivity, SettingsActivity.class));
				break;
			case R.id.menu_help:
				new MainDialogs(mainActivity, mainActivity.getGame()).openHelpDialog();
				break;
			case R.id.menu_bugtracker:
				final Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("https://github.com/meikpiep/holokenmod/issues"));
				mainActivity.startActivity(intent);
				break;
			default:
				break;
		}
		
		DrawerLayout drawerLayout = mainActivity.findViewById(R.id.container);
		drawerLayout.close();
		
		return true;
	}
}
