package com.holokenmod.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.holokenmod.Game;
import com.holokenmod.R;

class MainDialogs {
	private final MainActivity mainActivity;
	private final Game game;
	
	MainDialogs(MainActivity mainActivity, Game game) {
		this.mainActivity = mainActivity;
		this.game = game;
	}
	
	void newGameGridDialog() {
		Intent intent = new Intent(mainActivity, NewGameActivity.class);
		
		if (game.getGridUI() != null && game.getGrid() != null) {
			ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
					mainActivity,
					game.getGridUI(),
					"grid");
			
			mainActivity.startActivityForResult(intent, 0, options.toBundle());
		} else {
			mainActivity.startActivityForResult(intent, 0);
		}
	}
	
	void restartGameDialog() {
		if (!game.getGrid().isActive()) {
			return;
		}
		final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
		builder.setTitle(R.string.dialog_restart_title)
				.setMessage(R.string.dialog_restart_msg)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setNegativeButton(R.string.dialog_cancel, (dialog, id) -> dialog.cancel())
				.setPositiveButton(R.string.dialog_ok, (dialog, id) -> {
					game.clearUserValues();
					game.getGrid().setActive(true);
					mainActivity.startFreshGrid(true);
				})
				.show();
	}
	
	void openHelpDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
		final LayoutInflater inflater = LayoutInflater.from(mainActivity);
		final View layout = inflater.inflate(R.layout.dialog_help,
				mainActivity.findViewById(R.id.help_layout));
		builder.setTitle(R.string.help_section_title)
				.setView(layout)
				.setNeutralButton(R.string.about_section_title, (dialog, id) -> this
						.openAboutDialog())
				.setPositiveButton(R.string.dialog_ok, (dialog, id) -> dialog.cancel())
				.show();
	}
	
	void openAboutDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
		final LayoutInflater inflater = LayoutInflater.from(mainActivity);
		final View layout = inflater.inflate(R.layout.dialog_about,
				mainActivity.findViewById(R.id.about_layout));
		
		builder.setTitle(R.string.about_section_title)
				.setView(layout)
				.setNeutralButton(R.string.help_section_title, (dialog, id) -> this
						.openHelpDialog())
				.setPositiveButton(R.string.dialog_ok, (dialog, id) -> dialog.cancel())
				.show();
	}
}
