package com.holokenmod;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class CurrentGameSaver {
	private final File saveGameDirectory;
	
	public CurrentGameSaver(File saveGameDirectory) {
		this.saveGameDirectory = saveGameDirectory;
	}
	
	public void save() {
		int fileIndex;
		File filename;
		for (fileIndex = 0; ; fileIndex++) {
			filename = new File(saveGameDirectory, SaveGame.SAVEGAME_NAME_PREFIX_ + fileIndex);
			if (!filename.exists()) {
				break;
			}
		}
		try {
			this.copy(new File(saveGameDirectory, SaveGame.SAVEGAME_AUTO_NAME), filename);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void copy(final File src, final File dst) throws IOException {
		FileUtils.copyFile(src, dst);
	}
}
