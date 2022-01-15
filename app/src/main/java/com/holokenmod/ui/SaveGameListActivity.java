package com.holokenmod.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.holokenmod.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class SaveGameListActivity extends ListActivity {
	public static final String SAVEGAME_AUTO_NAME = "autosave";
	public static final String SAVEGAME_NAME_PREFIX_ = "savegame_";
	public boolean mCurrentSaved;
	
	private MaterialButton discardButton;
	private SaveGameListAdapter mAdapter;
	
	public SaveGameListActivity() {
	}
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		
		super.onCreate(savedInstanceState);
		
		if (!PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean("showfullscreen", false)) {
			this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		setContentView(R.layout.activity_savegame);
		final MaterialButton saveButton = findViewById(R.id.savebutton);
		discardButton = findViewById(R.id.discardbutton);
		TextView empty = findViewById(android.R.id.empty);
		ListView saveGameList = findViewById(android.R.id.list);
		
		saveGameList.setEmptyView(empty);
		this.mAdapter = new SaveGameListAdapter(this);
		saveGameList.setAdapter(this.mAdapter);
		
		saveButton.setOnClickListener(v -> {
			saveButton.setEnabled(false);
			currentSaveGame();
		});
		
		if (this.mCurrentSaved) {
			saveButton.setEnabled(false);
		}
		
		discardButton.setEnabled(false);
		if (mAdapter.getCount() != 0) {
			discardButton.setEnabled(true);
		}
		
		discardButton.setOnClickListener(v -> deleteAllGamesDialog());
	}
	
	public void deleteSaveGame(final File filename) {
		filename.delete();
		mAdapter.refreshFiles();
		mAdapter.notifyDataSetChanged();
	}
	
	public void deleteAllSaveGames() {
		for (final File file : getSaveGameFiles()) {
			file.delete();
		}
		
		mAdapter.refreshFiles();
		mAdapter.notifyDataSetChanged();
		
		discardButton.setEnabled(false);
	}
	
	@Nullable
	Collection<File> getSaveGameFiles() {
		final File dir = this.getFilesDir();
		
		return Arrays.asList(dir.listFiles((dir1, name) -> name.startsWith("savegame_")));
	}
	
	public void deleteGameDialog(final File filename) {
		new AlertDialog.Builder(SaveGameListActivity.this, R.style.AppTheme)
				.setTitle(R.string.dialog_delete_title)
				.setMessage(R.string.dialog_delete_msg)
				.setNegativeButton(R.string.dialog_cancel, (dialog, whichButton) -> dialog.cancel())
				.setPositiveButton(R.string.dialog_ok, (dialog, whichButton) -> SaveGameListActivity.this
						.deleteSaveGame(filename))
				.show();
	}
	
	public void deleteAllGamesDialog() {
		new AlertDialog.Builder(SaveGameListActivity.this, R.style.AppTheme)
				.setTitle(R.string.dialog_delete_all_title)
				.setMessage(R.string.dialog_delete_all_msg)
				.setNegativeButton(R.string.dialog_cancel, (dialog, whichButton) -> dialog.cancel())
				.setPositiveButton(R.string.dialog_ok, (dialog, whichButton) -> SaveGameListActivity.this
						.deleteAllSaveGames())
				.show();
	}
	
	public void loadSaveGame(final File filename) {
		final Intent i = new Intent().putExtra("filename", filename.getAbsolutePath());
		setResult(Activity.RESULT_OK, i);
		finish();
	}
	
	public void currentSaveGame() {
		this.mCurrentSaved = true;
		int fileIndex;
		File filename;
		for (fileIndex = 0; ; fileIndex++) {
			filename = new File(this.getFilesDir(), SAVEGAME_NAME_PREFIX_ + fileIndex);
			if (!filename.exists()) {
				break;
			}
		}
		try {
			this.copy(new File(this.getFilesDir(), SAVEGAME_AUTO_NAME), filename);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mAdapter.refreshFiles();
		this.mAdapter.notifyDataSetChanged();
	}
	
	void copy(final File src, final File dst) throws IOException {
		FileUtils.copyFile(src, dst);
	}
}