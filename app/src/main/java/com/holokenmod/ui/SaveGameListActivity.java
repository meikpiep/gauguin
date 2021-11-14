package com.holokenmod.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.holokenmod.R;
import com.holokenmod.Theme;
import com.holokenmod.options.ApplicationPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SaveGameListActivity extends ListActivity {
	public static final String SAVEGAME_AUTO_NAME = "autosave";
	public static final String SAVEGAME_NAME_PREFIX_ = "savegame_";
	public boolean mCurrentSaved;
	TextView empty;
	ListView saveGameList;
	ImageButton discardButton;
	private SaveGameListAdapter mAdapter;
	
	public SaveGameListActivity() {
	}
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean("showfullscreen", false)) {
			this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else {
			this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		
		setContentView(R.layout.activity_savegame);
		final Button saveButton = findViewById(R.id.savebutton);
		discardButton = findViewById(R.id.discardbutton);
		empty = findViewById(android.R.id.empty);
		saveGameList = findViewById(android.R.id.list);
		
		final Theme theme = ApplicationPreferences.getInstance().getTheme();
		
		this.findViewById(R.id.saveGameContainer).setBackgroundColor(
				theme.getBackgroundColor());
		if (theme == Theme.LIGHT) {
			saveButton.setTextColor(getResources().getColorStateList(R.color.text_button));
		} else if (theme == Theme.DARK) {
			saveButton.setTextColor(getResources().getColorStateList(R.color.text_button_dark));
		}
		
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
		final File[] allFiles = getSaveGameFiles();
		
		if (allFiles != null) {
			for (final File file : allFiles) {
				file.delete();
			}
		}
		
		mAdapter.refreshFiles();
		mAdapter.notifyDataSetChanged();
		
		discardButton.setEnabled(false);
	}
	
	@Nullable
	File[] getSaveGameFiles() {
		final File dir = this.getFilesDir();
		
		final File[] allFiles = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("savegame_");
			}
		});
		return allFiles;
	}
	
	public void deleteGameDialog(final File filename) {
		new AlertDialog.Builder(SaveGameListActivity.this)
				.setTitle(R.string.dialog_delete_title)
				.setMessage(R.string.dialog_delete_msg)
				.setNegativeButton(R.string.dialog_cancel, (dialog, whichButton) -> dialog.cancel())
				.setPositiveButton(R.string.dialog_ok, (dialog, whichButton) -> SaveGameListActivity.this
						.deleteSaveGame(filename))
				.show();
	}
	
	public void deleteAllGamesDialog() {
		new AlertDialog.Builder(SaveGameListActivity.this)
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
		final InputStream in = new FileInputStream(src);
		final OutputStream out = new FileOutputStream(dst);
		
		// Transfer bytes from in to out
		final byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}
	
}
