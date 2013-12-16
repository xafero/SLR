package com.xafero.slr.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FolderWatcher extends TimerTask implements Closeable {
	private File folder;
	private Timer timer;
	private FileListener listener;
	private HashMap<String, Long> lastModifieds;
	private HashMap<String, Long> lengths;

	public FolderWatcher(File folder, long milliseconds, FileListener listener) {
		this.folder = folder;
		this.timer = new Timer();
		this.listener = listener;
		this.timer.scheduleAtFixedRate(this, milliseconds, milliseconds);
		this.lastModifieds = new HashMap<String, Long>();
		this.lengths = new HashMap<String, Long>();
	}

	@Override
	public void run() {
		for (File file : folder.listFiles()) {
			if (!file.isFile())
				continue;
			String key = file.getAbsolutePath();
			long lmc;
			boolean lastModifiedChanged = false;
			if ((lmc = file.lastModified()) != getLong(lastModifieds, key)) {
				lastModifieds.put(key, lmc);
				lastModifiedChanged = true;
			}
			long ltc;
			boolean lengthChanged = false;
			if ((ltc = file.length()) != getLong(lengths, key)) {
				lengths.put(key, ltc);
				lengthChanged = true;
			}
			if (!lastModifiedChanged && !lengthChanged)
				continue;
			FileChange e = new FileChange(key,
					lastModifiedChanged ? lmc : null, lengthChanged ? ltc
							: null);
			listener.fileChanged(this, e);
		}
	}

	private static long getLong(Map<String, Long> map, String key) {
		return map.containsKey(key) ? map.get(key) : Long.MIN_VALUE;
	}

	@Override
	public void close() throws IOException {
		timer.purge();
		timer.cancel();
		lastModifieds.clear();
		lengths.clear();
	}

	public static interface FileListener {
		void fileChanged(FolderWatcher watcher, FileChange event);
	}

	public static class FileChange {
		public final String Key;
		public final Long LastModified;
		public final Long Length;

		public FileChange(String key, Long lastModified, Long length) {
			Key = key;
			LastModified = lastModified;
			Length = length;
		}

		@Override
		public String toString() {
			return "FileChange [Key=" + Key + ", LastModified=" + LastModified
					+ ", Length=" + Length + "]";
		}
	}
}