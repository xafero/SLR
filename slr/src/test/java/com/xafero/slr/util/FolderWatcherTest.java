package com.xafero.slr.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import com.xafero.slr.util.FolderWatcher.FileChange;
import com.xafero.slr.util.FolderWatcher.FileListener;

public class FolderWatcherTest {

	@Test
	public void testWatcher() throws InterruptedException, IOException {
		final File tf = new File("test.tmp");
		File root = IOHelper.currentDir();
		long ms = 10;
		final AtomicReference<FolderWatcher> wRef = new AtomicReference<FolderWatcher>();
		final AtomicBoolean ab = new AtomicBoolean();
		FileListener listener = new FileListener() {
			public void fileChanged(FolderWatcher watcher, FileChange event) {
				assertNotNull(watcher);
				assertNotNull(event);
				if (!event.Key.endsWith(".tmp"))
					return;
				assertEquals(tf.getAbsolutePath(), event.Key);
				try {
					wRef.get().close();
					ab.set(true);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
		FolderWatcher w = new FolderWatcher(root, ms, listener);
		wRef.set(w);
		tf.createNewFile();
		tf.deleteOnExit();
		Thread.sleep(21);
		assertTrue("Watcher was not called!", ab.get());
		tf.delete();
	}
}